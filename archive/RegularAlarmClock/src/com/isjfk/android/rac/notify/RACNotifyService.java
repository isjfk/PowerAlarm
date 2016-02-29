/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.rac.notify;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmService;
import com.isjfk.android.rac.activity.AlarmActivity;
import com.isjfk.android.rac.activity.DismissSnoozedConfirmActivity;
import com.isjfk.android.rac.activity.ScheduleListActivity;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.other.DayHourMinute;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.util.AndroidUtil;

/**
 * 闹铃通知服务。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-8-27
 */
public class RACNotifyService {

    private Context context;

    private NotificationManager notificationManager;

    /**
     * 是否取消通知再重新设置通知。
     * <li>true: 取消通知再重新设置通知</li>
     * <li>false: 只更新文本</li>
     * 默认: true
     */
    private boolean updateAll = true;

    /**
     * 是否显示状态栏滚动通知。
     * <li>true: 显示状态栏滚动通知</li>
     * <li>false: 不显示状态栏滚动通知<li>
     * 默认: true
     */
    private boolean showFlowNotify = true;

    /**
     * 构造闹铃通知服务。
     *
     * @param context 应用上下文
     */
    private RACNotifyService(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 获取闹铃通知服务。
     *
     * @param context 应用上下文
     * @return 闹铃通知服务
     */
    public static RACNotifyService getService(Context context) {
        return new RACNotifyService(context);
    }

    /**
     * 弹出下次闹铃通知消息。
     *
     * @param schedule 闹铃日程
     */
    public void popupNextAlarm(Schedule schedule) {
        String msg;
        DayHourMinute interval = RACUtil.getNextAlarmInterval(schedule.getTime());
        if (interval.day == 0) {
            msg = getString(
                    R.string.notifyPopupHourMinute,
                    schedule.getName(),
                    String.valueOf(interval.hour),
                    String.valueOf(interval.minute));
        } else {
            msg = getString(
                    R.string.notifyPopupDayHourMinute,
                    schedule.getName(),
                    String.valueOf(interval.day),
                    String.valueOf(interval.hour),
                    String.valueOf(interval.minute));
        }

        RACUtil.popupNotifyLong(getContext(), msg);
    }

    /**
     * 在通知栏显示下次闹铃通知。
     *
     * @param schedule 闹铃日程
     */
    public void notifySchedule(Schedule schedule) {
        if (RACContext.isAlarmTimeNotification()) {
            if (RACUtil.isExpired(schedule)) {
                return;
            }

            if (RACUtil.isSnoozed(schedule)) {
                notifySnoozed(R.string.notifyScheduleSnoozedFlow, schedule);
            } else {
                notifyScheduleNormal(schedule);
            }
        } else {
            cancel(schedule.getId());
        }
    }

    private void notifyScheduleNormal(Schedule schedule) {
        String alarmName = schedule.getName();
        Calendar alarmTime = schedule.getTime();

        String flowNotify = null;
        if (showFlowNotify) {
            flowNotify = getString(
                    R.string.notifyScheduleFlow,
                    alarmName,
                    RACTimeUtil.formatDateTimeWeek(alarmTime));
        }

        String scheduleTitle = getString(
                R.string.notifyScheduleTitle,
                RACTimeUtil.formatTime(alarmTime),
                alarmName);

        String scheduleDesc = null;
        String scheduleDate = RACTimeUtil.formatDateWeekShort(alarmTime);
        DayHourMinute interval = RACUtil.getNextAlarmInterval(schedule.getTime());
        if (interval.day == 0) {
            scheduleDesc = getString(
                            R.string.notifyScheduleDescHourMinute,
                            scheduleDate,
                            String.valueOf(interval.hour),
                            String.valueOf(interval.minute));
        } else {
            scheduleDesc = getString(
                            R.string.notifyScheduleDescDayHourMinute,
                            scheduleDate,
                            String.valueOf(interval.day),
                            String.valueOf(interval.hour),
                            String.valueOf(interval.minute));
        }

        PendingIntent activity = PendingIntent.getActivity(
                getContext(),
                0,
                new Intent(getContext(), ScheduleListActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.alarm_notification);
        builder.setTicker(flowNotify);
        builder.setContentTitle(scheduleTitle);
        builder.setContentText(scheduleDesc);
        builder.setContentIntent(activity);
        builder.setOngoing(true);

        if (updateAll) {
            cancel(schedule.getId());
        }
        notify(schedule.getId(), builder.build());
    }

    /**
     * 在通知栏显示闹铃响铃通知。
     *
     * @param service 闹铃服务
     * @param schedule 闹铃日程
     */
    public void notifyAlarm(Service service, Schedule schedule) {
        Intent intent = new Intent(getContext(), AlarmActivity.class);
        AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, schedule);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent activity = PendingIntent.getActivity(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String flowNotify = null;
        if (showFlowNotify) {
            flowNotify = getString(R.string.notifyAlarmFlow, schedule.getName());
        }

        String alarmTitle = getString(R.string.notifyAlarmTitle, schedule.getName());
        String alarmDesc = getString(R.string.notifyAlarmDesc);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.alarm_notification);
        builder.setTicker(flowNotify);
        builder.setContentTitle(alarmTitle);
        builder.setContentText(alarmDesc);
        builder.setContentIntent(activity);
        builder.setOngoing(true);

        cancel(schedule.getId());
        service.startForeground(schedule.getId(), builder.build());
    }

    /**
     * 清除通知栏上的闹铃响铃通知。
     *
     * @param service 闹铃服务
     * @param schedule 闹铃日程
     */
    public void cancelAlarmNotify(Service service, Schedule schedule) {
        service.stopForeground(true);
    }

    /**
     * 在通知栏显示闹铃小睡通知。
     *
     * @param schedule 闹铃日程
     */
    public void notifySnoozed(Schedule schedule) {
        notifySnoozed(R.string.notifySnoozedFlow, schedule);
    }

    private void notifySnoozed(int resId, Schedule schedule) {
        String flowNotify = null;
        if (showFlowNotify) {
            flowNotify = getString(
                    resId,
                    schedule.getName(),
                    RACTimeUtil.formatDateTimeWeek(schedule.getTime()));
        }

        String snoozeTitle = getString(R.string.notifyScheduleSnoozedTitle, schedule.getName());

        DayHourMinute interval = RACUtil.getNextAlarmInterval(schedule.getTime());
        if (interval.hour != 0) {
            interval.minute += interval.hour * 60;
        }
        String snoozeDesc = getString(R.string.notifyScheduleSnoozedDesc, String.valueOf(interval.minute));

        Intent intent = new Intent(getContext(), DismissSnoozedConfirmActivity.class);
        AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, schedule);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent activity = PendingIntent.getActivity(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.alarm_notification);
        builder.setTicker(flowNotify);
        builder.setContentTitle(snoozeTitle);
        builder.setContentText(snoozeDesc);
        builder.setContentIntent(activity);
        builder.setOngoing(true);

        if (updateAll) {
            cancel(schedule.getId());
        }
        notify(schedule.getId(), builder.build());
    }

    /**
     * 在通知栏显示闹铃超时通知。
     *
     * @param schedule 闹铃日程
     */
    public void notifyTimeout(Schedule schedule) {
        Intent intent = new Intent(RegularAlarmService.ACTION_DONOTHING);
        AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, schedule);

        PendingIntent broadcast = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String timeoutDateTime = RACTimeUtil.formatDateTimeWeek(schedule.getTime());
        String flowNotify = null;
        if (showFlowNotify) {
            flowNotify = getString(R.string.notifyTimeoutFlow, schedule.getName(), timeoutDateTime);
        }

        String timeoutTitle = getString(R.string.notifyTimeoutTitle, schedule.getName());
        String timeoutDesc = getString(R.string.notifyTimeoutDesc, timeoutDateTime);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.alarm_notification);
        builder.setTicker(flowNotify);
        builder.setContentTitle(timeoutTitle);
        builder.setContentText(timeoutDesc);
        builder.setContentIntent(broadcast);
        builder.setAutoCancel(true);

        if (updateAll) {
            cancel(schedule.getId());
        }
        notify(schedule.getId(), builder.build());
    }

    /**
     * 清除通知栏上的闹铃通知。
     *
     * @param scheduleId 闹铃日程ID
     */
    public void cancelNotify(int scheduleId) {
        cancel(scheduleId);
    }

    /**
     * 清除通知栏上的所有闹铃通知。
     */
    public void cancelAllNotify() {
        cancelAll();
    }

    /**
     * 设置是否取消状态栏通知后再重新设置通知。
     *
     * @param updateAll true: 取消通知再重新设置通知; false: 只更新文本
     * @return RACNotifyService
     */
    public RACNotifyService updateAll(boolean updateAll) {
        this.updateAll = updateAll;
        return this;
    }

    /**
     * 设置取消状态栏通知后再重新设置通知。
     *
     * @return RACNotifyService
     */
    public RACNotifyService updateAll() {
        updateAll = true;
        return this;
    }

    /**
     * 设置只更新状态栏通知文本。
     *
     * @return RACNotifyService
     */
    public RACNotifyService updateOnlyText() {
        updateAll = false;
        return this;
    }

    /**
     * 设置是否显示状态栏滚动通知。
     *
     * @param showFlowNotify true: 显示状态栏滚动通知; false: 不显示状态栏滚动通知
     * @return RACNotifyService
     */
    public RACNotifyService showFlowNotify(boolean showFlowNotify) {
        this.showFlowNotify = showFlowNotify;
        return this;
    }

    /**
     * 设置显示状态栏滚动通知。
     *
     * @return RACNotifyService
     */
    public RACNotifyService showFlowNotify() {
        showFlowNotify = true;
        return this;
    }

    /**
     * 设置不显示状态栏滚动通知。
     *
     * @return RACNotifyService
     */
    public RACNotifyService hideFlowNotify() {
        showFlowNotify = false;
        return this;
    }

    private Context getContext() {
        return context;
    }

    private NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private String getString(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    private void notify(int id, Notification notification) {
        getNotificationManager().notify(id, notification);
    }

    private void cancel(int id) {
        getNotificationManager().cancel(id);
    }

    private void cancelAll() {
        getNotificationManager().cancelAll();
    }

}
