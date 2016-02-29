/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 *
 * Default DateSlider which allows for an easy selection of a date
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;

import com.isjfk.android.rac.DateSlider;
import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.RACConstant;
import com.isjfk.android.rac.common.RACTimeDesc;
import com.isjfk.android.rac.common.RACUtil;

/**
 * 月日选择对话框。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-19
 */
public class DayMonthSliderDialog extends DateSlider {

    public DayMonthSliderDialog(
            Context context,
            OnDateSetListener l,
            Calendar calendar) {
        super(context, R.layout.daymonth_slider, l, format(calendar), getMinTime(), getMaxTime());
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.DateSlider#setTitle()
     */
    protected void setTitle() {
        if (mTitleText != null) {
            mTitleText.setText(RACTimeDesc.descDayMonth(getTime()));
        }
    }

    private static Calendar format(Calendar calendar) {
        // set to an leap year, since we only need day and month, which year is not mattered
        calendar.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        return calendar;
    }

    private static Calendar getMinTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        RACUtil.clearTime(cal);

        return cal;
    }

    private static Calendar getMaxTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        RACUtil.clearTime(cal);

        return cal;
    }

}
