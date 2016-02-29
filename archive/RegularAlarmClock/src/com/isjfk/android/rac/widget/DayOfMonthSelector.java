/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.common.RACContext;

/**
 * 按月生效的日期规则选择控件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-19
 */
public class DayOfMonthSelector extends CalendarSelector {

    public DayOfMonthSelector(Context context) {
        super(context);
    }

    public DayOfMonthSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<Integer> getSelectedDayOfMonth() {
        List<Integer> selectedDayOfMonth = new ArrayList<Integer>();
        for (DayMonthYear dayMonthYear : selectedDays) {
            selectedDayOfMonth.add(dayMonthYear.day);
        }
        return selectedDayOfMonth;
    }

    public void setSelectedDayOfMonth(List<Integer> dayOfMonthList) {
        List<DayMonthYear> selectedDays = new ArrayList<DayMonthYear>();
        for (Integer dayOfMonth : dayOfMonthList) {
            selectedDays.add(new DayMonthYear(dayOfMonth, month, year));
        }
        setSelectedDays(selectedDays);
    }

    @Override
    protected void init() {
        showWeek = false;
        showWeekOfYear = false;
        showBeforAfterDays = false;

        super.init();
    }

    @Override
    protected void initDefaultDate() {
        if (RACContext.isFirstDaySunday()) {
            // first day of 1/2012 is Sunday
            year = 2012;
            month = 0;
        } else {
            // first day of 10/2012 is Monday
            year = 2012;
            month = 9;
        }
    }

}
