/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.other;

/**
 * 月日日期。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-12-23
 */
public class DayMonth implements Comparable<DayMonth> {

    public int day;
    public int month;

    /**
     * 构造月日日期。
     *
     * @param day 日
     * @param month 月
     */
    public DayMonth(int day, int month) {
        this.day = day;
        this.month = month;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(DayMonth that) {
        if (this.month > that.month) {
            return 1;
        } else if (this.month == that.month) {
            if (this.day > that.day) {
                return 1;
            } else if (this.day == that.day) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

}