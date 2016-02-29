/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmService;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.util.AndroidUtil;

/**
 * 解除小睡状态闹铃确认界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class DismissSnoozedConfirmActivity extends Activity {

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            Log.e("error create DismissSnoozedConfirmActivity: intent not exists");
            finish();
            return;
        }

        Schedule schedule = AndroidUtil.getExtra(intent, RegularAlarmService.SCHEDULE);
        if (schedule == null) {
            Log.e("error create DismissSnoozedConfirmActivity: schedule not exists");
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(
                R.string.notifyDismissSnoozedConfirm,
                schedule.getName()));
        builder.setPositiveButton(R.string.buttonYes, new DismissSnoozedListener(this, schedule));
        builder.setNegativeButton(R.string.buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        builder.show();
    }

    private class DismissSnoozedListener implements DialogInterface.OnClickListener {
        private Context context;
        private Schedule schedule;
        private DismissSnoozedListener(Context context, Schedule schedule) {
            this.context = context;
            this.schedule = schedule;
        }
        public void onClick(DialogInterface dialog, int id) {
            Intent intent = new Intent(context, RegularAlarmService.class);
            intent.setAction(RegularAlarmService.ACTION_ALARM_DISMISS_SNOOZED);
            AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, schedule);
            context.startService(intent);

            setResult(RESULT_OK);
            finish();
        }
    }

}
