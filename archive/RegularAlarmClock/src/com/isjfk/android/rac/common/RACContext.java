/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmService;
import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.AlarmRule.Columns.RingEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.VibrateEnum;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;


/**
 * 规则闹钟上下文。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class RACContext {

    /** 包名 */
    private static String pkgName = "com.isjfk.android.rac";

    /** 版本 */
    private static String version = "1.0";

    /** 版本内部编号 */
    private static String build = "1";

    /** 是否可debug */
    private static boolean debuggable = false;

    /** 系统配置项 */
    private static SharedPreferences pref = null;

    /** 应用资源 */
    private static Resources resources;

    /** 新建闹铃时分钟的初始化边界。单位：分钟 */
    private static int timeMinuteSegment = 30;

    /** 闹铃日程长度。单位：天 */
    private static int scheduleDays = 14;

    /** 强制删除n小时前的闹铃日程记录。单位：小时 */
    private static int scheduleForceDelHour = 24;

    /** 如果没有可响铃的闹铃日程，每天自动刷新闹铃日程的时间 */
    private static int scheduleRefreshHour = 2;
    private static int scheduleRefreshMinute = 0;

    /** 响铃时按钮禁用时间，此时间内用户无法点击以防止误操作。单位：秒 */
    private static int alarmButtonDisableTimeout = 2;

    /** 响铃时如果用户无动作，默认响铃时间，不能为0（铃声时间）。单位：秒 */
    private static int defaultAlarmTime = 60;

    /** 响铃时如果用户无动作，且选择的响铃时长为歌曲长度时，默认失败保护响铃时间。单位：秒 */
    private static int failSafeAlarmTime = 600;

    /** 响铃时如果用户无动作，自动小睡次数（自动小睡2次则一共响铃3次） */
    private static int autoSnoozeTimes = 2;

    /** 首次使用规则闹铃应用的时间 */
    private static Calendar firstUsedTime = Calendar.getInstance();

    /** 开始显示广告的时间。单位：天 */
    private static int showAdDays = 60;

    /** 显示广告的概率。单位：1/x */
    private static int showAdRate = 2;

    /** 显示响铃广告的概率。单位：1/x */
    private static int showAlarmAdRate = 10;

    /** 广告关键字 */
    private static int maxAdKeywordSize = 5;
    private static Set<String> adKeywords = new HashSet<String>();

    // preference configurations

    /** 播放铃声的通道 */
    private static int streamType = AudioManager.STREAM_ALARM;

    /** 播放TTS语音的通道 */
    private static int ttsStreamType = AudioManager.STREAM_MUSIC;

    /** 闹铃最大音量 */
    private static int maxVolume = 7;

    /** 默认淡入时间，单位“秒” */
    private static int defaultFadeInTime = 60;

    /** 响铃方式 */
    private static int ring = RingEnum.RING;

    private static RingtoneConfig globalRingtoneConfig;

    /** 响铃时是否说出闹铃名字 */
    private static boolean speakAlarmName = false;

    /** 振动方式 */
    private static int vibrate = VibrateEnum.VIBRATE;

    /** 响铃时间。单位：秒 */
    private static int alarmTime = 60;

    /** 小睡时间。单位：分钟 */
    private static int snoozeTime = 10;

    /** 时间是否采用24小时制 */
    private static boolean time24HourFormat = false;

    /** 每周的第一天是不是星期天 */
    private static boolean firstDaySunday = true;

    /** 闹铃界面显示超大的Snooze按钮 */
    private static boolean largeSnoozeButton = false;

    /** 如果距离传感器探测到距离物体太近，则禁用闹铃按钮 */
    private static boolean disableButtonInPocket = true;

    /** 是否在通知栏显示下次响铃通知 */
    private static boolean alarmTimeNotification = true;

    /** 是否显示弹出窗口提示 */
    private static boolean popupNotification = true;

    /** 是否显示未激活的闹铃日程 */
    private static boolean showInactiveSchedules = false;

    /**
     * 初始化规则闹钟上下文。
     *
     * @param context Android上下文
     */
    public static void init(Context context) {
        RACTimeContext.init(context);

        pkgName = context.getPackageName();
        AndroidUtil.setIntentExtraPrefix(pkgName);

        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(getPkgName(), 0);
            version = pkgInfo.versionName;
            build = String.valueOf(pkgInfo.versionCode);
        } catch (NameNotFoundException e) {
            Log.e("error get package info for " + getPkgName(), e);
        }
        debuggable = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ringStr = pref.getString(ConfigEnum.Ring, String.valueOf(ring));
        try {
            ring = Integer.parseInt(ringStr);
        } catch (NumberFormatException e) {
            Log.e("error parse ring: " + ringStr, e);
        }

        resources = context.getResources();

        RingtoneConfig defaultRingtoneConfig = new RingtoneConfig();
        String ringtoneStr = pref.getString(ConfigEnum.Ringtone, RingtoneConfig.encode(defaultRingtoneConfig));
        globalRingtoneConfig = RingtoneConfig.decode(ringtoneStr);
        if (globalRingtoneConfig == null) {
            globalRingtoneConfig = defaultRingtoneConfig;
        }

        Uri ringtoneUri = globalRingtoneConfig.getRingtone();
        if (!RACUtil.isRingtoneValid(context, ringtoneUri)) {
            globalRingtoneConfig.setType(RingtoneConfig.TYPE_DEFAULT);
            globalRingtoneConfig.setRingtone(getDefaultRingtone());
        }

        speakAlarmName = pref.getBoolean(ConfigEnum.SpeakAlarmName, speakAlarmName);

        String vibrateStr = pref.getString(ConfigEnum.Vibrate, String.valueOf(vibrate));
        try {
            vibrate = Integer.parseInt(vibrateStr);
        } catch (NumberFormatException e) {
            Log.e("error parse vibrate: " + vibrateStr, e);
        }

        String alarmTimeStr = pref.getString(ConfigEnum.AlarmTime, String.valueOf(getDefaultAlarmTime()));
        try {
            alarmTime = Integer.parseInt(alarmTimeStr);
        } catch (NumberFormatException e) {
            Log.e("error parse alarmTime: " + alarmTimeStr, e);
        }

        String snoozeTimeStr = pref.getString(ConfigEnum.SnoozeTime, String.valueOf(snoozeTime));
        try {
            snoozeTime = Integer.parseInt(snoozeTimeStr);
        } catch (NumberFormatException e) {
            Log.e("error parse snoozeTime: " + snoozeTimeStr, e);
        }

        String autoSnoozeTimesStr = pref.getString(ConfigEnum.AutoSnoozeTimes, String.valueOf(autoSnoozeTimes));
        try {
            autoSnoozeTimes = Integer.parseInt(autoSnoozeTimesStr);
        } catch (NumberFormatException e) {
            Log.e("error parse autoSnoozeTimes: " + autoSnoozeTimesStr, e);
        }

        time24HourFormat = pref.getBoolean(ConfigEnum.Time24HourFormat, time24HourFormat);
        firstDaySunday = pref.getBoolean(ConfigEnum.FirstDaySunday, firstDaySunday);

        largeSnoozeButton = pref.getBoolean(ConfigEnum.LargeSnoozeButton, largeSnoozeButton);
        disableButtonInPocket = pref.getBoolean(ConfigEnum.DisableButtonInPocket, disableButtonInPocket);
        alarmTimeNotification = pref.getBoolean(ConfigEnum.AlarmTimeNotification, alarmTimeNotification);
        popupNotification = pref.getBoolean(ConfigEnum.PopupNotification, popupNotification);
        showInactiveSchedules = pref.getBoolean(ConfigEnum.ShowInactiveSchedules, showInactiveSchedules);

        String streamTypeStr = pref.getString(ConfigEnum.StreamType, String.valueOf(streamType));
        try {
            streamType = Integer.parseInt(streamTypeStr);
        } catch (NumberFormatException e) {
            Log.e("error parse streamType: " + streamTypeStr, e);
        }

        String ttsStreamTypeStr = pref.getString(ConfigEnum.TTSStreamType, String.valueOf(ttsStreamType));
        try {
            ttsStreamType = Integer.parseInt(ttsStreamTypeStr);
        } catch (NumberFormatException e) {
            Log.e("error parse ttsStreamType: " + ttsStreamTypeStr, e);
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(streamType);

        String firstUsedTimeStr = pref.getString(ConfigEnum.FirstUsedTime, null);
        if (firstUsedTimeStr == null) {
            firstUsedTimeStr = RACTimeUtil.stdFormatDateTime(Calendar.getInstance());
            pref.edit().putString(ConfigEnum.FirstUsedTime, firstUsedTimeStr).commit();
        }

        firstUsedTime = RACTimeUtil.stdParseDateTime(firstUsedTimeStr);
        if (firstUsedTime == null) {
            firstUsedTime = Calendar.getInstance();
            firstUsedTimeStr = RACTimeUtil.stdFormatDateTime(firstUsedTime);
            pref.edit().putString(ConfigEnum.FirstUsedTime, firstUsedTimeStr).commit();
        }
        if (firstUsedTime.compareTo(Calendar.getInstance()) > 0) {
            firstUsedTime = Calendar.getInstance();
            firstUsedTimeStr = RACTimeUtil.stdFormatDateTime(firstUsedTime);
            pref.edit().putString(ConfigEnum.FirstUsedTime, firstUsedTimeStr).commit();
        }
    }

    public static void resetAlarm(Context context) {
        Intent intent = new Intent(context, RegularAlarmService.class);
        intent.setAction(RegularAlarmService.ACTION_RAC_STARTUP);
        context.startService(intent);
    }

    public static Resources getResources() {
        return resources;
    }

    public static String getVersion() {
        return version;
    }

    public static String getBuild() {
        return build;
    }

    public static boolean isDebuggable() {
        return debuggable;
    }

    public static boolean isReleaseMode() {
        return !isDebuggable();
    }

    public static boolean isAdVersion() {
        return getPkgName().contains("racad");
    }

    public static String getPkgName() {
        return pkgName;
    }

    public static Uri getDefaultRingtone() {
        return Uri.parse("android.resource://" + getPkgName() + "/" + R.raw.default_ringtone);
    }

    public static int getTimeMinuteSegment() {
        return timeMinuteSegment;
    }

    public static int getScheduleDays() {
        return scheduleDays;
    }

    public static int getScheduleForceDelHour() {
        return scheduleForceDelHour;
    }

    public static int getScheduleRefreshHour() {
        return scheduleRefreshHour;
    }

    public static int getScheduleRefreshMinute() {
        return scheduleRefreshMinute;
    }

    public static int getAlarmButtonDisableTimeout() {
        return alarmButtonDisableTimeout;
    }

    public static int getDefaultAlarmTime() {
        return defaultAlarmTime;
    }

    public static int getFailSafeAlarmTime() {
        return failSafeAlarmTime;
    }

    public static int getAutoSnoozeTimes() {
        return autoSnoozeTimes;
    }

    private static boolean shouldShowAd() {
        if (isAdVersion()) {
            Calendar time = Calendar.getInstance();
            time.add(Calendar.DAY_OF_MONTH, -showAdDays);
            return time.compareTo(firstUsedTime) > 0;
        }
        return false;
    }

    public static boolean isShowAd() {
        if (shouldShowAd()) {
            return new Random().nextInt(showAdRate) == 0;
        }
        return false;
    }

    public static boolean isShowAlarmAd() {
        if (shouldShowAd()) {
            return new Random().nextInt(showAlarmAdRate) == 0;
        }
        return false;
    }

    public static Set<String> getAdKeywords() {
        return adKeywords;
    }

    public static void setAdKeywordsForSchedule(List<Schedule> schedules) {
        if (isAdVersion()) {
            adKeywords.clear();
            for (Schedule schedule : schedules) {
                if (JavaUtil.isNotEmpty(schedule.getName())) {
                    adKeywords.add(schedule.getName());
                    if (adKeywords.size() >= maxAdKeywordSize) {
                        break;
                    }
                }
            }
        }
    }

    public static void setAdKeywordsForAlarmRule(List<AlarmRule> alarmRules) {
        if (isAdVersion()) {
            adKeywords.clear();
            for (AlarmRule alarmRule : alarmRules) {
                if (JavaUtil.isNotEmpty(alarmRule.getName())) {
                    adKeywords.add(alarmRule.getName());
                    if (adKeywords.size() >= maxAdKeywordSize) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 判断应用是否需要显示首次启动帮助。
     *
     * @return 需要显示首次启动帮助返回true，否则返回false
     */
    public static boolean isShowFirstStartupHelp() {
        if (JavaUtil.isEmpty(pref.getString(ConfigEnum.LastStartupBuild, null))) {
            pref.edit().putString(ConfigEnum.LastStartupBuild, getBuild()).commit();
            return true;
        }

        return false;
    }

    /**
     * 判断应用是否需要显示新特性提示。
     *
     * @return 需要显示新特性提示返回true，否则返回false
     */
    public static boolean isShowWhatsNew() {
        if (!JavaUtil.equals(pref.getString(ConfigEnum.LastStartupBuild, null), getBuild())) {
            pref.edit().putString(ConfigEnum.LastStartupBuild, getBuild()).commit();
            return true;
        }

        return false;
    }



    // preference configurations

    public static int getStreamType() {
        return streamType;
    }

    public static int getTtsStreamType() {
        return ttsStreamType;
    }

    public static int getMaxVolume() {
        return maxVolume;
    }

    public static int getDefaultFadeInTime() {
        return defaultFadeInTime;
    }

    public static int getRing() {
        return ring;
    }

    public static RingtoneConfig getGlobalRingtoneConfig() {
        return globalRingtoneConfig;
    }

    public static boolean isSpeakAlarmName() {
        return speakAlarmName;
    }

    public static int getVibrate() {
        return vibrate;
    }

    public static int getAlarmTime() {
        return alarmTime;
    }

    public static int getSnoozeTime() {
        return snoozeTime;
    }

    public static boolean isTime24HourFormat() {
        return time24HourFormat;
    }

    public static boolean isFirstDaySunday() {
        return firstDaySunday;
    }

    public static boolean isLargeSnoozeButton() {
        return largeSnoozeButton;
    }

    public static boolean isDisableButtonInPocket() {
        return disableButtonInPocket;
    }

    public static boolean isAlarmTimeNotification() {
        return alarmTimeNotification;
    }

    public static boolean isPopupNotification() {
        return popupNotification;
    }

    public static boolean isShowInactiveSchedules() {
        return showInactiveSchedules;
    }


    public static interface ConfigEnum {
        String Ring = "ring";
        String Volume = "volume";
        String Ringtone = "ringtone";
        String SpeakAlarmName = "speakAlarmName";
        String Vibrate = "vibrate";
        String AlarmTime = "alarmTime";
        String SnoozeTime = "snoozeTime";
        String AutoSnoozeTimes = "autoSnoozeTimes";
        String DateFormat = "dateFormat";
        String Time24HourFormat = "time24HourFormat";
        String FirstDaySunday = "firstDaySunday";
        String LargeSnoozeButton = "largeSnoozeButton";
        String DisableButtonInPocket = "disableButtonInPocket";
        String AlarmTimeNotification = "alarmTimeNotification";
        String PopupNotification = "popupNotification";
        String ShowInactiveSchedules = "showInactiveSchedules";
        String StreamType = "streamType";
        String TTSStreamType = "ttsStreamType";

        String FirstUsedTime = "firstUsedTime";
        String LastStartupBuild = "lastStartupBuild";
    }

    public interface PrefRingEnum {
        Integer DEFAULT = 0;
        Integer RING = 1;
        Integer NORING = 2;
    }

    public interface PrefVibrateEnum {
        Integer DEFAULT = 0;
        Integer VIBRATE = 1;
        Integer NOVIBRATE = 2;
    }

    public static interface PrefAlarmTimeEnum {
        Integer RINGTONE_LENGTH = -1;
    }

}
