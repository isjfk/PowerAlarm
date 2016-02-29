/**
 * (C) Copyright InfiniteSpace Studio, 2011-2013. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.text.ParseException;
import java.util.Calendar;

import android.content.res.Resources;

import com.isjfk.android.rac.R;

/**
 * 规则闹钟时间工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2013-4-19
 */
public class RACTimeUtil {

    /**
     * 将内部使用的时间字符串转换为日期。
     * 内部使用的时间字符串为24小时制，格式“HH:mm”。
     *
     * @param timeStr 字符串表示的24小时制时间
     * @return 日期
     */
    public static Calendar stdParseTime(String timeStr) {
        Calendar time = Calendar.getInstance();
        try {
            time.setTime(RACTimeContext.getTimeFormat24().parse(timeStr));
        } catch (ParseException e) {
            Log.e("error parse time " + timeStr, e);
        }

        return time;
    }

    /**
     * 将日期格式化为内部使用的标准日期字符串。
     * 内部使用的标准日期字符串格式“yyyy-MM-dd”。
     *
     * @param cal 日期
     * @return 内部使用的标准日期字符串
     */
    public static String stdFormatDate(Calendar cal) {
        return RACTimeContext.getDateFormat().format(cal.getTime());
    }

    /**
     * 将内部使用的标准日期字符串格式化为日期。
     * 内部使用的标准日期字符串格式“yyyy-MM-dd”。
     *
     * @param dateStr 内部使用的标准日期时间字符串
     * @return 日期，如果无法格式化则返回null
     */
    public static Calendar stdParseDate(String dateStr) {
        Calendar cal = Calendar.getInstance();
        try
        {
            cal.setTime(RACTimeContext.getDateFormat().parse(dateStr));
        } catch (ParseException e) {
            Log.e("error parse date " + dateStr, e);
            return null;
        }
        return cal;
    }

    /**
     * 将日期格式化为内部使用的标准日期时间字符串。
     * 内部使用的标准日期时间字符串为24小时制，格式“yyyy-MM-dd HH:mm”。
     *
     * @param cal 日期
     * @return 内部使用的标准日期时间字符串
     */
    public static String stdFormatDateTime(Calendar cal) {
        return RACTimeContext.getDateTimeFormat24().format(cal.getTime());
    }

    /**
     * 将内部使用的标准日期时间字符串格式化为日期。
     * 内部使用的标准日期时间字符串为24小时制，格式“yyyy-MM-dd HH:mm”。
     *
     * @param dateTimeStr 内部使用的标准日期时间字符串
     * @return 日期，如果无法格式化则返回null
     */
    public static Calendar stdParseDateTime(String dateTimeStr) {
        Calendar cal = Calendar.getInstance();
        try
        {
            cal.setTime(RACTimeContext.getDateTimeFormat24().parse(dateTimeStr));
        } catch (ParseException e) {
            Log.e("error parse dateTime " + dateTimeStr, e);
            return null;
        }
        return cal;
    }

    public static String formatTime(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        if (RACContext.isTime24HourFormat()) {
            return RACTimeContext.getTimeFormat24().format(cal.getTime());
        } else {
            if (RACUtil.isAM(cal)) {
                return res.getString(R.string.timeAM, RACTimeContext.getTimeFormat12().format(cal.getTime()));
            } else {
                return res.getString(R.string.timePM, RACTimeContext.getTimeFormat12().format(cal.getTime()));
            }
        }
    }

    public static String formatTimeOnly(Calendar cal) {
        if (cal == null) {
            return "";
        }

        if (RACContext.isTime24HourFormat()) {
            return RACTimeContext.getTimeFormat24().format(cal.getTime());
        } else {
            return RACTimeContext.getTimeFormat12().format(cal.getTime());
        }
    }

    public static String formatDate(Calendar cal) {
        if (cal == null) {
            return "";
        }

        return RACTimeContext.getDisplayDateFormat().format(cal.getTime());
    }

    public static String formatDateTime(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(R.string.dateTime, formatTime(cal), formatDate(cal));
    }

    public static String formatDateTimeWeek(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(
                R.string.dateTimeWeek,
                formatTime(cal),
                formatDate(cal),
                RACUtil.getDayOfWeekName(res, cal));
    }

    public static String formatDateWeekShort(Calendar cal) {
        Resources res = RACContext.getResources();
        if ((cal == null) || (res == null)) {
            return "";
        }

        return res.getString(
                R.string.dateWeekShort,
                formatDate(cal),
                RACUtil.getDayOfWeekNameShort(res, cal));
    }

}
