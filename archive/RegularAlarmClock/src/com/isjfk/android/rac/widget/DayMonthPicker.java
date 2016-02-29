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
import com.isjfk.android.rac.common.RACConstant;
import com.isjfk.android.rac.common.RACTimeDesc;

/**
 * 月日选择器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-17
 */
public class DayMonthPicker extends Button {

    protected Calendar date;

    protected OnDateSetListener dateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DateSlider view, Calendar selectedDate) {
            setDayMonth(
                    selectedDate.get(Calendar.DAY_OF_MONTH),
                    selectedDate.get(Calendar.MONTH));
        }
    };

    public DayMonthPicker(Context context) {
        super(context);
        init();
    }

    public DayMonthPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayMonthPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setDayMonth(int day, int month) {
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

    protected void init() {
        date = Calendar.getInstance();
        date.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        refreshView();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DayMonthSliderDialog(getContext(), dateSetListener, date).show();
            }
        });
    }

    protected void refreshView() {
        setText(RACTimeDesc.descDayMonth(date));
    }

}
