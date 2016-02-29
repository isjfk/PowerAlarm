/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.isjfk.android.rac.DateSlider;
import com.isjfk.android.rac.DateSlider.OnDateSetListener;
import com.isjfk.android.rac.common.RACTimeDesc;

/**
 * 年月日选择器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-21
 */
public class DayMonthYearPicker extends Button {

    protected Calendar date;
    protected OnDayMonthYearChangedListener onDayMonthYearChangedListener = null;

    protected OnDateSetListener dateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DateSlider view, Calendar selectedDate) {
            setDayMonthYear(
                    selectedDate.get(Calendar.DAY_OF_MONTH),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.YEAR));
            if (onDayMonthYearChangedListener != null) {
                onDayMonthYearChangedListener.onDayMonthYearChanged(DayMonthYearPicker.this);
            }
        }
    };

    public DayMonthYearPicker(Context context) {
        super(context);
        init();
    }

    public DayMonthYearPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayMonthYearPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OnDayMonthYearChangedListener getOnDayMonthYearChangedListener() {
        return onDayMonthYearChangedListener;
    }

    public void setOnDayMonthYearChangedListener(OnDayMonthYearChangedListener onDayMonthYearChangedListener) {
        this.onDayMonthYearChangedListener = onDayMonthYearChangedListener;
    }

    public void setDayMonthYear(int day, int month, int year) {
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        refreshView();
    }

    public int getDay() {
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return date.get(Calendar.MONTH);
    }

    public int getYear() {
        return date.get(Calendar.YEAR);
    }

    protected void init() {
        date = Calendar.getInstance();
        refreshView();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DayMonthYearSliderDialog(getContext(), dateSetListener, date).show();
            }
        });
    }

    protected void refreshView() {
        setText(RACTimeDesc.descDateShort(date));
    }

    public static interface OnDayMonthYearChangedListener {
        public void onDayMonthYearChanged(DayMonthYearPicker picker);
    }

}
