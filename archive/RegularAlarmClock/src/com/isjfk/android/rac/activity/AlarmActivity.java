/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmService;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 响铃界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class AlarmActivity extends Activity {

    private static final int RESULT_CANCEL = 0;
    private static final int RESULT_DISMISS = 1;
    private static final int RESULT_SNOOZE = 2;

    private int result = RESULT_SNOOZE;
    private Schedule schedule = null;

    private Button dismissButton;
    private Button snoozeButton;

    /** 延时启用按钮属性 */
    private static final int MSG_TIMEOUT_ENABLE_BUTTON = 1;
    private Handler delayEnableButtonHandler = new DelayEnableButtonHandler(this);

    /**
     * 延时启用按钮Handler。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-17
     */
    private static class DelayEnableButtonHandler extends WeakReferenceHandler<AlarmActivity> {
        public DelayEnableButtonHandler(AlarmActivity activity) {
            super(activity);
        }

        public void onMessage(AlarmActivity activity, Message msg) {
            switch (msg.what) {
            case MSG_TIMEOUT_ENABLE_BUTTON:
                activity.enableButton();
                break;
            }
        }
    }

    /** 距离传感器属性 */
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private float proximityEnableRange;
    private float proximityDisableRange;
    private SensorEventListener proximityEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do nothing
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            if ((event != null) && (event.values != null) && (event.values.length > 0)) {
                float distance = event.values[0];
                if (distance > proximityEnableRange) {
                    delayEnableButton();
                } else if (distance < proximityDisableRange) {
                    cancelDelayEnableButton();
                    disableButton();
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(RegularAlarmService.ACTION_ALARM_CANCEL)) {
                result = RESULT_CANCEL;
                finish();
            }
        }
    };

    /**
     * 构造响铃Activity。
     */
    public AlarmActivity() {
        super();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (RACContext.isLargeSnoozeButton()) {
            setContentView(R.layout.alarm_view_largesnooze);
        } else {
            setContentView(R.layout.alarm_view);
        }

        dismissButton = (Button) findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                result = RESULT_DISMISS;
                sendResult();
                finish();
            }
        });

        snoozeButton = (Button) findViewById(R.id.snoozeButton);
        snoozeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                result = RESULT_SNOOZE;
                sendResult();
                finish();
            }
        });

        // 初始化距离传感器
        if (RACContext.isDisableButtonInPocket()) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (proximitySensor != null) {
                float proximityMaxRange = proximitySensor.getMaximumRange();
                proximityEnableRange = proximityMaxRange * 0.6f;
                proximityDisableRange = proximityMaxRange * 0.4f;
            }
        }

        showIntent(getIntent());
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showIntent(intent);
    }

    private void showIntent(Intent intent) {
        schedule = AndroidUtil.getExtra(intent, RegularAlarmService.SCHEDULE);
        if (schedule != null) {
            ((TextView) findViewById(R.id.alarmTime)).setText(RACTimeUtil.formatTime(schedule.getTime()));
            ((TextView) findViewById(R.id.alarmDesc)).setText(schedule.getName());
        } else {
            result = RESULT_DISMISS;
            sendResult();
            finish();
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        result = RESULT_SNOOZE;

        registerReceiver(broadcastReceiver, new IntentFilter(RegularAlarmService.ACTION_ALARM_CANCEL));

        if (proximitySensor != null) {
            sensorManager.registerListener(
                    proximityEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        delayEnableButton();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(broadcastReceiver);

        if (proximitySensor != null) {
            sensorManager.unregisterListener(proximityEventListener);
        }

        cancelDelayEnableButton();
    }

    /**
     * 将响铃结果发送给RegularAlarmService。
     */
    private void sendResult() {
        if (result != RESULT_CANCEL) {
            Intent intent = new Intent(this, RegularAlarmService.class);
            switch (result) {
            case RESULT_DISMISS:
                intent.setAction(RegularAlarmService.ACTION_ALARM_DISMISS);
                startService(intent);
                break;
            case RESULT_SNOOZE:
                intent.setAction(RegularAlarmService.ACTION_ALARM_SNOOZE);
                startService(intent);
                break;
            }
        }

        // 确保只发送一次响铃结果
        result = RESULT_CANCEL;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // do nothing
    }

    /**
     * 启用按钮。
     */
    public void enableButton() {
        dismissButton.setEnabled(true);
        snoozeButton.setEnabled(true);
    }

    /**
     * 禁用按钮。
     */
    public void disableButton() {
        dismissButton.setEnabled(false);
        snoozeButton.setEnabled(false);
    }

    /**
     * 延迟启用按钮。
     */
    protected void delayEnableButton() {
        delayEnableButtonHandler.sendMessageDelayed(
                delayEnableButtonHandler.obtainMessage(MSG_TIMEOUT_ENABLE_BUTTON),
                RACContext.getAlarmButtonDisableTimeout() * 1000);
    }

    /**
     * 解除延迟启用按钮。
     */
    protected void cancelDelayEnableButton() {
        delayEnableButtonHandler.removeMessages(MSG_TIMEOUT_ENABLE_BUTTON);
    }

}
