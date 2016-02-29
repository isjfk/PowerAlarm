/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.other;

/**
 * 日小时分钟日期。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-01-19
 */
public class DayHourMinute {

    public int day;
    public int hour;
    public int minute;

    /**
     * 构造日小时分钟日期。
     *
     * @param day 日
     * @param hour 小时
     * @param minute 分钟
     */
    public DayHourMinute(int day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DayHourMinute) {
            DayHourMinute that = (DayHourMinute) o;
            return (this.minute == that.minute) && (this.hour == that.hour) && (this.day == that.day);
        }
        return false;
    }

}