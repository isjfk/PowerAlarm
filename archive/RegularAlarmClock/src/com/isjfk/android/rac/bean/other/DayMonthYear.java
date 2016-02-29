/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.other;

import java.util.Calendar;

import com.isjfk.android.rac.common.RACUtil;


/**
 * 年月日日期。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-12-23
 */
public class DayMonthYear implements Comparable<DayMonthYear> {

    public int day;
    public int month;
    public int year;

    /**
     * 构造年月日日期。
     *
     * @param day 日
     * @param month 月
     * @param year 年
     */
    public DayMonthYear(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DayMonthYear) {
            DayMonthYear that = (DayMonthYear) o;
            return (this.year == that.year) && (this.month == that.month) && (this.day == that.day);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(DayMonthYear that) {
        int result = this.year - that.year;
        if (result != 0) {
            return result;
        }
        result = this.month - that.month;
        if (result != 0) {
            return result;
        }
        return this.day - that.day;
    }

    public int compareTo(Calendar cal) {
        DayMonthYear that = new DayMonthYear(
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR));
        return compareTo(that);
    }

    /**
     * 增加指定天数。
     *
     * @param days 天数
     */
    public void addDay(int days) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        RACUtil.clearTime(cal);

        cal.add(Calendar.DAY_OF_MONTH, days);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

}