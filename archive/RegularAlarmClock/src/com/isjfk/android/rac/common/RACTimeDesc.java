/**
 * (C) Copyright InfiniteSpace Studio, 2011-2013. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.util.Calendar;
import java.util.regex.Pattern;

import android.content.res.Resources;

import com.isjfk.android.rac.R;
import com.isjfk.android.util.JavaUtil;

/**
 * 规则闹钟时间工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2013-4-19
 */
public class RACTimeDesc {

    private static final Pattern DATE_PATTERN_DDMM = Pattern.compile("[dd|DD].*[mm|MM]");
    private static final Pattern DATE_PATTERN_MMDD = Pattern.compile("[mm|MM].*[dd|DD]");
    private static final Pattern DATE_PATTERN_DDMMYYYY = Pattern.compile("[dd|DD].*[mm|MM].*yyyy");
    private static final Pattern DATE_PATTERN_MMDDYYYY = Pattern.compile("[mm|MM].*[dd|DD].*yyyy");
    private static final Pattern DATE_PATTERN_YYYYMMDD = Pattern.compile("yyyy.*[mm|MM].*[dd|DD]");

    public static String descTime(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        if (RACContext.isTime24HourFormat()) {
            String hourStr = RACTimeContext.getHourFormat24().format(cal.getTime());
            String minuteStr = RACTimeContext.getMinuteFormat().format(cal.getTime());
            Integer minute = JavaUtil.toInteger(minuteStr);

            if (Integer.valueOf(0).equals(minute)) {
                return res.getString(R.string.descTimeNoMinute, hourStr);
            } else {
                return res.getString(R.string.descTime, hourStr, minuteStr);
            }
        } else {
            String hourStr = RACTimeContext.getHourFormat12().format(cal.getTime());
            String minuteStr = RACTimeContext.getMinuteFormat().format(cal.getTime());
            Integer minute = JavaUtil.toInteger(minuteStr);

            String timeDescStr;
            if (Integer.valueOf(0).equals(minute)) {
                timeDescStr = res.getString(R.string.descTimeNoMinute, hourStr);
            } else {
                timeDescStr = res.getString(R.string.descTime, hourStr, minuteStr);
            }

            return res.getString(RACUtil.isAM(cal) ? R.string.descTimeAM : R.string.descTimePM, timeDescStr);
        }
    }

    public static String descDay(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getStringArray(R.array.dayOfMonth)[cal.get(Calendar.DAY_OF_MONTH) - 1];
    }

    public static String descMonthShort(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getStringArray(R.array.monthNameShort)[cal.get(Calendar.MONTH)];
    }

    public static String descMonth(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getStringArray(R.array.monthName)[cal.get(Calendar.MONTH)];
    }

    public static String descYear(Calendar cal) {
        if (cal == null) {
            return "";
        }

        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public static String descDayMonthShort(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(getDayMonthStrRes(), descDay(cal), descMonthShort(cal));
    }

    public static String descDayMonth(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(getDayMonthStrRes(), descDay(cal), descMonth(cal));
    }

    public static String descDateShort(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(getDayMonthYearStrRes(), descDay(cal), descMonthShort(cal), descYear(cal));
    }

    public static String descDate(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(getDayMonthYearStrRes(), descDay(cal), descMonth(cal), descYear(cal));
    }

    private static int getDayMonthStrRes() {
        if (DATE_PATTERN_MMDD.matcher(RACTimeContext.getDisplayDateFormat().toPattern()).find()) {
            return R.string.dayMonth_mmdd;
        } else if (DATE_PATTERN_DDMM.matcher(RACTimeContext.getDisplayDateFormat().toPattern()).find()) {
            return R.string.dayMonth_ddmm;
        }
        return R.string.dayMonth_mmdd;
    }

    private static int getDayMonthYearStrRes() {
        if (DATE_PATTERN_MMDDYYYY.matcher(RACTimeContext.getDisplayDateFormat().toPattern()).find()) {
            return R.string.dayMonthYear_mmddyyyy;
        } else if (DATE_PATTERN_DDMMYYYY.matcher(RACTimeContext.getDisplayDateFormat().toPattern()).find()) {
            return R.string.dayMonthYear_ddmmyyyy;
        } else if (DATE_PATTERN_YYYYMMDD.matcher(RACTimeContext.getDisplayDateFormat().toPattern()).find()) {
            return R.string.dayMonthYear_yyyymmdd;
        }
        return R.string.dayMonthYear_mmddyyyy;
    }

}
