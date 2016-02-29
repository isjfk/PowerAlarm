/**
 * (C) Copyright InfiniteSpace Studio, 2011-2013. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isjfk.android.rac.common.RACContext.ConfigEnum;

/**
 * 规则闹钟时间上下文。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2013-4-19
 */
public class RACTimeContext {

    private static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

    private static SimpleDateFormat dateTimeFormat24 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static SimpleDateFormat timeFormat12 = new SimpleDateFormat("hh:mm", Locale.US);
    private static SimpleDateFormat timeFormat24 = new SimpleDateFormat("HH:mm", Locale.US);
    private static SimpleDateFormat hourFormat12 = new SimpleDateFormat("h", Locale.US);
    private static SimpleDateFormat hourFormat24 = new SimpleDateFormat("H", Locale.US);
    private static SimpleDateFormat minuteFormat = new SimpleDateFormat("m", Locale.US);

    /** 显示给用户的日期格式 */
    private static SimpleDateFormat displayDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.US);

    /**
     * 初始化时间上下文。
     * 时区变更后应调用此方法修改时间格式化工具时区。
     *
     * @param context Android上下文
     */
    public static void init(Context context) {
        dateTimeFormat24 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        timeFormat12 = new SimpleDateFormat("hh:mm", Locale.US);
        timeFormat24 = new SimpleDateFormat("HH:mm", Locale.US);
        hourFormat12 = new SimpleDateFormat("h", Locale.US);
        hourFormat24 = new SimpleDateFormat("H", Locale.US);
        minuteFormat = new SimpleDateFormat("m", Locale.US);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        displayDateFormat = new SimpleDateFormat(pref.getString(ConfigEnum.DateFormat, DEFAULT_DATE_FORMAT), Locale.US);
    }

    public static SimpleDateFormat getDateTimeFormat24() {
        return dateTimeFormat24;
    }

    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static SimpleDateFormat getTimeFormat12() {
        return timeFormat12;
    }

    public static SimpleDateFormat getTimeFormat24() {
        return timeFormat24;
    }

    public static SimpleDateFormat getHourFormat12() {
        return hourFormat12;
    }

    public static SimpleDateFormat getHourFormat24() {
        return hourFormat24;
    }

    public static SimpleDateFormat getMinuteFormat() {
        return minuteFormat;
    }

    public static SimpleDateFormat getDisplayDateFormat() {
        return displayDateFormat;
    }

}
