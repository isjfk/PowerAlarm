/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.isjfk.android.rac.RegularAlarmPlayer.RegularAlarmPlayerListener;
import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.AlarmRule.Columns.AlarmTimeEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.RingEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.VibrateEnum;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACContext.PrefAlarmTimeEnum;
import com.isjfk.android.rac.common.RACContext.PrefRingEnum;
import com.isjfk.android.rac.common.RACContext.PrefVibrateEnum;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.rac.common.RACTimeContext;
import com.isjfk.android.rac.common.RACTimeDesc;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.notify.RACNotifyService;
import com.isjfk.android.util.AlarmManagerUtil;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 规则闹铃服务。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-12
 */
public class RegularAlarmService extends RegularAlarmDataService {

    public static final String ACTION_ANDROID_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
    public static final String ACTION_ANDROID_INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER";
    public static final String ACTION_DONOTHING = "com.isjfk.android.rac.DONOTHING";
    public static final String ACTION_RAC_STARTUP = "com.isjfk.android.rac.STARTUP";
    public static final String ACTION_SCHEDULE_CHANGED = "com.isjfk.android.rac.SCHEDULE_CHANGED";
    public static final String ACTION_SCHEDULE_REFRESH = "com.isjfk.android.rac.SCHEDULE_REFRESH";
    public static final String ACTION_ALARM_ALERT = "com.isjfk.android.rac.ALARM_ALERT";
    public static final String ACTION_ALARM_DISMISS = "com.isjfk.android.rac.ALARM_DISMISS";
    public static final String ACTION_ALARM_SNOOZE = "com.isjfk.android.rac.ALARM_SNOOZE";
    public static final String ACTION_ALARM_TIMEOUT = "com.isjfk.android.rac.ALARM_TIMEOUT";
    public static final String ACTION_ALARM_ACTIVITY = "com.isjfk.android.rac.ALARM_ACTIVITY";
    public static final String ACTION_ALARM_DISMISS_SNOOZED = "com.isjfk.android.rac.ALARM_DISMISS_SNOOZED";
    public static final String ACTION_NOTIFY_REFRESH = "com.isjfk.android.rac.NOTIFY_REFRESH";

    /** 闹铃取消，关闭闹铃界面 */
    public static final String ACTION_ALARM_CANCEL = "com.isjfk.android.rac.ALARM_CANCEL";

    public static final String SCHEDULE = "schedule";
    public static final String SCHEDULE_ID = "scheduleId";
    public static final String SCHEDULE_LIST = "scheduleList";

    private RegularAlarmPlayer alarmPlayer;
    private Schedule alarmSchedule = null;

    /**
     * 构造闹铃服务。
     */
    public RegularAlarmService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmPlayer = new RegularAlarmPlayer(this);
        alarmPlayer.addListener(new RegularAlarmPlayerListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onStop() {
                // 关闭闹铃界面
                sendBroadcast(new Intent(ACTION_ALARM_CANCEL));

                if (isInAlarm()) {
                    // 处理闹铃超时
                    Intent intent = new Intent(RegularAlarmService.this, RegularAlarmService.class);
                    intent.setAction(ACTION_ALARM_TIMEOUT);
                    startService(intent);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RegularAlarmWakeLock.release();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.RegularAlarmDataService#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startId) {
        if (intent == null) {
            // do nothing
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || ACTION_ANDROID_INSTALL_REFERRER.equals(intent.getAction())
                || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())
                || ACTION_ANDROID_LOCALE_CHANGED.equals(intent.getAction())) {
            // rebuildSchedules()会触发ACTION_SCHEDULE_CHANGED事件，所以此处不需要调用scheduleNextAlarm()
            RACTimeContext.init(this);
            configScheduleRefresh();
            rebuildSchedules();
        } else if (ACTION_RAC_STARTUP.equals(intent.getAction())
                || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
            configScheduleRefresh();
            refreshScheduleList();
            if (!isInAlarm()) {
                scheduleNextAlarm(false, null);
            }
        } else if (ACTION_SCHEDULE_REFRESH.equals(intent.getAction())) {
            refreshScheduleList();
            if (!isInAlarm()) {
                scheduleNextAlarm(false, null);
            }
        } else if (ACTION_SCHEDULE_CHANGED.equals(intent.getAction())) {
            cancelAll();
            scheduleNextAlarm(true, null);
        } else if (ACTION_ALARM_ALERT.equals(intent.getAction())) {
            int scheduleId = AndroidUtil.getExtra(intent, SCHEDULE_ID, -1);
            if (scheduleId == -1) {
                Log.e("schedule id not exist for broadcast event " + ACTION_ALARM_ALERT);
                scheduleNextAlarm(true, null);
            } else {
                Schedule schedule = querySchedule(scheduleId);
                if (schedule == null) {
                    Log.e("schedule " + scheduleId + " not exist, cannot alarm");
                    scheduleNextAlarm(true, null);
                } else if (isInAlarm()) {
                    // should not happen
                    Log.e("now alarming schedule "
                            + alarmSchedule.getId()
                            + ", cannot alarm another schedule "
                            + scheduleId);
                    finishAlarm(schedule);
                } else {
                    try {
                        alarm(schedule);
                    } catch (Exception e) {
                        Log.e("error start schedule " + schedule.getName(), e);
                        dismissAlarm();

                        cancelAll();
                        scheduleNextAlarm(true, null);
                    }
                }
            }
        } else if (ACTION_ALARM_DISMISS.equals(intent.getAction())) {
            if (isInAlarm()) {
                dismissAlarm();
            } else {
                Log.e("not in alarm, cannot dismiss");
                cancelAll();
            }
            scheduleNextAlarm(true, null);
        } else if (ACTION_ALARM_SNOOZE.equals(intent.getAction())) {
            Schedule schedule = null;
            if (isInAlarm()) {
                schedule = snoozeAlarm();
            } else {
                Log.e("not in alarm, cannot snooze");
                cancelAll();
            }
            scheduleNextAlarm(true, schedule);
        } else if (ACTION_ALARM_TIMEOUT.equals(intent.getAction())) {
            Schedule schedule = null;
            if (isInAlarm()) {
                schedule = timeoutAlarm();
            } else {
                Log.e("not in alarm, cannot timeout");
                cancelAll();
            }
            scheduleNextAlarm(true, schedule);
        } else if (ACTION_ALARM_DISMISS_SNOOZED.equals(intent.getAction())) {
            Schedule schedule = AndroidUtil.getExtra(intent, SCHEDULE);
            if (!isInAlarm()) {
                Schedule nextSchedule = findNextRingableSchedule();
                dismissSnoozedAlarm(schedule);
                if ((nextSchedule != null) && (nextSchedule.getId() == schedule.getId())) {
                    scheduleNextAlarm(true, null);
                } else {
                    scheduleNextAlarm(false, null);
                }
            } else if (alarmSchedule.getId() != schedule.getId()) {
                dismissSnoozedAlarm(schedule);
            } else {
                Log.e("schedule " + schedule.getId() + " is now alarming, cannot dismiss snoozed");
            }
        } else if (ACTION_NOTIFY_REFRESH.equals(intent.getAction())) {
            List<Schedule> notifiedScheduleList = AndroidUtil.getExtra(intent, SCHEDULE_LIST);
            refreshNotify(notifiedScheduleList);
        }

        stopIfNotInAlarm();
    }

    /**
     * 如果当前未响铃则停止服务。
     */
    private void stopIfNotInAlarm() {
        if (!isInAlarm()) {
            stopSelf();
        }
    }

    /**
     * 设置下一个闹铃并显示下次响铃通知。
     *
     * @param showNotify 是否显示下次响铃通知
     * @param notifiedSchedule 已显示通知的闹铃，不需要重复显示通知
     */
    private void scheduleNextAlarm(boolean showNotify, Schedule notifiedSchedule) {
        // 在状态栏显示的通知列表
        List<Schedule> notifyBarScheduleList = new ArrayList<Schedule>();

        List<Schedule> scheduleList = findRingableScheduleList();
        if (JavaUtil.isNotEmpty(scheduleList)) {
            RACNotifyService notifySrv = RACNotifyService.getService(this);

            Schedule nextSchedule = scheduleList.get(0);
            setScheduleTimer(nextSchedule);

            if (showNotify) {
                notifySrv.popupNextAlarm(nextSchedule);
            }

            for (Schedule schedule : scheduleList) {
                if (schedule.equals(notifiedSchedule)) {
                    // 如果闹铃已显示通知则仅加入定时刷新列表
                    notifyBarScheduleList.add(schedule);
                } else if (schedule.equals(nextSchedule)) {
                    // 下次响铃闹铃要显示状态栏通知，且根据showNotify决定是否在状态栏显示滚动通知
                    notifySrv.showFlowNotify(showNotify).notifySchedule(schedule);
                    notifyBarScheduleList.add(schedule);
                } else if (RACUtil.isSnoozed(schedule)) {
                    // 其余小睡闹铃也要显示状态栏通知，但不显示滚动通知
                    notifySrv.hideFlowNotify().notifySchedule(schedule);
                    notifyBarScheduleList.add(schedule);
                }
            }
        }

        // 配置状态栏通知自动刷新
        if (JavaUtil.isNotEmpty(notifyBarScheduleList)) {
            configNotifyRefresh(notifyBarScheduleList);
        } else {
            cancelNotifyRefresh();
        }
    }

    /**
     * 设置闹铃日程定时器。
     *
     * @param schedule 闹铃日程
     */
    private void setScheduleTimer(Schedule schedule) {
        Intent intent = new Intent(ACTION_ALARM_ALERT);
        AndroidUtil.putExtra(intent, SCHEDULE_ID, schedule.getId());

        PendingIntent broadcast = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManagerUtil.setExact(am, AlarmManager.RTC_WAKEUP, schedule.getTime().getTimeInMillis(), broadcast);
    }

    /**
     * 取消闹铃日程定时器。
     */
    private void cancelScheduleTimer() {
        PendingIntent broadcast = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(ACTION_ALARM_ALERT),
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(broadcast);
    }

    /**
     * 配置闹铃状态栏通知自动刷新。
     *
     * @param notifiedScheduleList 已显示通知的闹铃日程
     */
    private void configNotifyRefresh(List<Schedule> notifiedScheduleList) {
        Calendar refreshTime = Calendar.getInstance();
        refreshTime.add(Calendar.MINUTE, 1);
        refreshTime.set(Calendar.SECOND, 0);
        refreshTime.set(Calendar.MILLISECOND, 100);

        Intent intent = new Intent(ACTION_NOTIFY_REFRESH);
        AndroidUtil.putExtra(intent, SCHEDULE_LIST, notifiedScheduleList);

        PendingIntent broadcast = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(
                AlarmManager.RTC,
                refreshTime.getTimeInMillis(),
                60 * 1000,
                broadcast);
    }

    /**
     * 取消闹状态栏铃通知自动刷新。
     */
    private void cancelNotifyRefresh() {
        PendingIntent broadcast = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(ACTION_NOTIFY_REFRESH),
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(broadcast);
    }

    /**
     * 配置闹铃日程自动刷新。
     */
    private void configScheduleRefresh() {
        Calendar currentTime = Calendar.getInstance();
        Calendar refreshTime = (Calendar) currentTime.clone();
        refreshTime.set(Calendar.HOUR_OF_DAY, RACContext.getScheduleRefreshHour());
        refreshTime.set(Calendar.MINUTE, RACContext.getScheduleRefreshMinute());
        if (refreshTime.compareTo(currentTime) <= 0) {
            refreshTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        PendingIntent broadcast = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(ACTION_SCHEDULE_REFRESH),
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                refreshTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                broadcast);
    }

    /**
     * 刷新闹铃日程。
     */
    private void refreshScheduleList() {
        queryAllSchedule();
    }

    /**
     * 刷新闹铃状态栏通知。
     *
     * @param notifiedScheduleList 已显示通知的闹铃日程列表
     */
    private void refreshNotify(List<Schedule> notifiedScheduleList) {
        if (JavaUtil.isEmpty(notifiedScheduleList)) {
            return;
        }

        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.updateOnlyText().hideFlowNotify();

        for (Schedule schedule : notifiedScheduleList) {
            if (isInAlarm() && (alarmSchedule.getId() == schedule.getId())) {
                // 不更新正在响铃的闹铃通知
                continue;
            }

            notifySrv.notifySchedule(schedule);
        }
    }

    /**
     * 当前是否正在闹铃。
     *
     * @return 当前正在闹铃返回true，否则返回false
     */
    private boolean isInAlarm() {
        return alarmSchedule != null;
    }

    /**
     * 响铃。
     *
     * @param schedule 闹铃日程
     * @param timeoutTimes 超时次数，0表示首次设置
     */
    private void alarm(Schedule schedule) {
        this.alarmSchedule = schedule;

        AlarmRule alarmRule = queryAlarmRule(schedule.getAlarmRuleId());
        if (alarmRule != null) {
            configRingtone(alarmRule);
            configSpeakAlarmName(schedule);
            configVibrate(alarmRule);
            configAlarmTime(alarmRule);
        } else {
            String errMsg = "alarmRule not exist: " + schedule.getAlarmRuleId();
            Log.e(errMsg);
            throw new RACException(errMsg);
        }

        alarmPlayer.play();

        Intent intent = new Intent(ACTION_ALARM_ACTIVITY);
        AndroidUtil.putExtra(intent, SCHEDULE, schedule);
        sendBroadcast(intent);

        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.notifyAlarm(this, schedule);
    }

    private void configRingtone(AlarmRule alarmRule) {
        if (RingEnum.DEFAULT.equals(alarmRule.getRing())) {
            if (PrefRingEnum.RING.equals(RACContext.getRing())) {
                alarmPlayer.setRing(true);
                alarmPlayer.configRingtone(alarmRule.getRingtone());
            } else if (PrefRingEnum.NORING.equals(RACContext.getRing())) {
                alarmPlayer.setRing(false);
            }
        } else if (RingEnum.RING.equals(alarmRule.getRing())) {
            alarmPlayer.setRing(true);
            alarmPlayer.configRingtone(alarmRule.getRingtone());
        } else if (RingEnum.NORING.equals(alarmRule.getRing())) {
            alarmPlayer.setRing(false);
        }
    }

    private void configSpeakAlarmName(Schedule schedule) {
        if (RACContext.isSpeakAlarmName()) {
            alarmPlayer.setTtsText(getResources().getString(
                    R.string.speakAlarmNameText,
                    schedule.getName(),
                    RACTimeDesc.descTime(schedule.getTime())));
        }
    }

    private void configVibrate(AlarmRule alarmRule) {
        if (VibrateEnum.DEFAULT.equals(alarmRule.getVibrate())) {
            if (PrefVibrateEnum.VIBRATE.equals(RACContext.getVibrate())) {
                alarmPlayer.setVibrate(true);
            } else if (PrefVibrateEnum.NOVIBRATE.equals(RACContext.getVibrate())) {
                alarmPlayer.setVibrate(false);
            }
        } else if (VibrateEnum.VIBRATE.equals(alarmRule.getVibrate())) {
            alarmPlayer.setVibrate(true);
        } else if (VibrateEnum.NOVIBRATE.equals(alarmRule.getVibrate())) {
            alarmPlayer.setVibrate(false);
        }
    }

    private void configAlarmTime(AlarmRule alarmRule) {
        if (AlarmTimeEnum.DEFAULT.equals(alarmRule.getAlarmTime())) {
            if (PrefAlarmTimeEnum.RINGTONE_LENGTH.equals(RACContext.getAlarmTime())) {
                if (Boolean.TRUE.equals(alarmPlayer.getRing())) {
                    alarmPlayer.setPlayTime(RACContext.getFailSafeAlarmTime());
                    alarmPlayer.setLooping(false);
                } else {
                    alarmPlayer.setPlayTime(RACContext.getDefaultAlarmTime());
                    alarmPlayer.setLooping(false);
                }
            } else {
                alarmPlayer.setPlayTime(RACContext.getAlarmTime());
                alarmPlayer.setLooping(true);
            }
        } else if (AlarmTimeEnum.RINGTONE_LENGTH.equals(alarmRule.getAlarmTime())) {
            if (Boolean.TRUE.equals(alarmPlayer.getRing())) {
                alarmPlayer.setPlayTime(RACContext.getFailSafeAlarmTime());
                alarmPlayer.setLooping(false);
            } else {
                alarmPlayer.setPlayTime(RACContext.getDefaultAlarmTime());
                alarmPlayer.setLooping(false);
            }
        } else {
            alarmPlayer.setPlayTime(alarmRule.getAlarmTime());
            alarmPlayer.setLooping(true);
        }
    }

    /**
     * 停止响铃。
     *
     * @return 被停止的闹铃日程，如果为null表示当前未响铃
     */
    private Schedule stopAlarm() {
        Schedule schedule = alarmSchedule;

        alarmSchedule = null;
        alarmPlayer.stop();

        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.cancelAlarmNotify(this, schedule);

        return schedule;
    }

    /**
     * 用户要求停止正在响铃的闹铃。
     */
    private void dismissAlarm() {
        Schedule schedule = stopAlarm();

        finishAlarm(schedule);
    }

    /**
     * 用户要求暂时停止正在响铃的闹铃并设置推迟一段时间后再次响铃。
     *
     * @return 被推迟的闹铃日程
     */
    private Schedule snoozeAlarm() {
        Schedule schedule = stopAlarm();

        schedule.setTime(RACUtil.getSnoozedTime(schedule.getTime()));
        schedule.setTimeoutTimes(1);

        updateSchedule(schedule);

        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.notifySnoozed(schedule);

        return schedule;
    }

    /**
     * 用户无响应，暂时停止正在响铃的闹铃并设置推迟一段时间后再次响铃。
     * 如果重复响铃超过指定次数则停止响铃并删除闹铃日程。
     *
     * @return 被推迟或者停止的闹铃日程
     */
    private Schedule timeoutAlarm() {
        Schedule schedule = stopAlarm();

        RACNotifyService notifySrv = RACNotifyService.getService(this);

        schedule.setTimeoutTimes(schedule.getTimeoutTimes() + 1);
        if (schedule.getTimeoutTimes() <= RACContext.getAutoSnoozeTimes()) {
            schedule.setTime(RACUtil.getSnoozedTime(schedule.getTime()));

            updateSchedule(schedule);
            notifySrv.notifySnoozed(schedule);
        } else {
            finishAlarm(schedule);
            notifySrv.notifyTimeout(schedule);
        }

        return schedule;
    }

    /**
     * 解除推迟响铃的闹铃。
     *
     * @param schedule 要解除的闹铃日程
     */
    private void dismissSnoozedAlarm(Schedule schedule) {
        finishAlarm(schedule);
    }

    /**
     * 结束闹铃，取消通知栏通知并删除闹铃日程。
     *
     * @param scheduleId 闹铃日程ID
     */
    private void finishAlarm(Schedule schedule) {
        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.cancelNotify(schedule.getId());

        deleteSchedule(schedule.getId());

        // 如果闹铃日程已过期，需要将其删除或禁用
        AlarmRule alarmRule = queryAlarmRule(schedule.getAlarmRuleId());
        if ((alarmRule != null) && alarmRule.isExpired()) {
            if (alarmRule.isDelAfterExpired()) {
                alarmRuleDao.delete(getContentResolver(), alarmRule.getId());
            } else {
                alarmRuleDao.updateEnabled(getContentResolver(), alarmRule.getId(), false);
            }
        }
    }

    /**
     * 清除所有闹铃、定时器和通知栏通知。
     */
    private void cancelAll() {
        cancelAlarm();
        cancelScheduleTimer();
        cancelNotifyRefresh();

        RACNotifyService notifySrv = RACNotifyService.getService(this);
        notifySrv.cancelAllNotify();
    }

    /**
     * 停止正在响铃的闹铃和待响铃的闹铃，并关闭闹铃提示界面。
     */
    private void cancelAlarm() {
        Schedule schedule = stopAlarm();
        if (schedule != null) {
            finishAlarm(schedule);
            sendBroadcast(new Intent(ACTION_ALARM_CANCEL));
        }
    }

    /**
     * 删除闹铃日程。
     *
     * @param scheduleId 需要删除的闹铃日程ID
     */
    private void deleteSchedule(int scheduleId) {
        scheduleDao.delete(getContentResolver(), scheduleId);
    }

    /**
     * 修改闹铃日程。
     *
     * @param schedule 闹铃日程
     * @return 见ResultCode中的结果码
     */
    private int updateSchedule(Schedule schedule) {
        if (scheduleDao.update(getContentResolver(), schedule) == 1) {
            return ResultCode.SUCCESS;
        } else {
            Log.e("error update schedule");
            return ResultCode.FAILED;
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.RegularAlarmDataService#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // not used
        return null;
    }

}
