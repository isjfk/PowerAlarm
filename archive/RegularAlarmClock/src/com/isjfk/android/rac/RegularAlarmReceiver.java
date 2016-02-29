/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.isjfk.android.rac.activity.AlarmActivity;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.util.AndroidUtil;

/**
 * 规则闹铃广播消息Receiver。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-13
 */
public class RegularAlarmReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (RegularAlarmService.ACTION_ALARM_ACTIVITY.equals(intent.getAction())) {
            Schedule schedule = AndroidUtil.getExtra(intent, RegularAlarmService.SCHEDULE);
            showAlertActivity(context, schedule);
        } else {
            if (RegularAlarmService.ACTION_ALARM_ALERT.equals(intent.getAction())) {
                RegularAlarmWakeLock.acquire(context);
            }

            // forward to service
            intent.setClass(context, RegularAlarmService.class);
            context.startService(intent);
        }
    }

    /**
     * 显示闹铃Activity。
     *
     * @param context 应用上下文
     * @param schedule 闹铃日程
     */
    private void showAlertActivity(Context context, Schedule schedule) {
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        Intent intent = new Intent(context, AlarmActivity.class);
        AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, schedule);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        context.startActivity(intent);
    }

}
