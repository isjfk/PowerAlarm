/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;

import com.isjfk.android.rac.DateSlider;
import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.RACTimeDesc;

/**
 * 年月日选择对话框。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-19
 */
public class DayMonthYearSliderDialog extends DateSlider {

    public DayMonthYearSliderDialog(
            Context context,
            OnDateSetListener l,
            Calendar calendar,
            Calendar minTime,
            Calendar maxTime) {
        super(context, R.layout.daymonthyear_slider, l, calendar, minTime, maxTime);
    }

    public DayMonthYearSliderDialog(Context context, OnDateSetListener l, Calendar calendar) {
        super(context, R.layout.daymonthyear_slider, l, calendar);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.DateSlider#setTitle()
     */
    protected void setTitle() {
        if (mTitleText != null) {
            mTitleText.setText(RACTimeDesc.descDateShort(getTime()));
        }
    }

}
