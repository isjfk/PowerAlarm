/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACUtil;

/**
 * 日期选择空间。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-17
 */
public class CalendarSelector extends TableLayout {

    protected boolean showWeek = true;
    protected boolean showWeekOfYear = true;
    protected boolean showBeforAfterDays = true;

    protected int month;
    protected int year;

    protected List<DayMonthYear> selectedDays = new ArrayList<DayMonthYear>();

    public CalendarSelector(Context context) {
        super(context);
        init();
    }

    public CalendarSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setDate(int month, int year) {
        this.month = month;
        this.year = year;
        refreshView();
    }

    public boolean isShowWeek() {
        return showWeek;
    }

    public void setShowWeek(boolean showWeek) {
        this.showWeek = showWeek;
        refreshView();
    }

    public boolean isShowWeekOfYear() {
        return showWeekOfYear;
    }

    public void setShowWeekOfYear(boolean showWeekOfYear) {
        this.showWeekOfYear = showWeekOfYear;
        refreshView();
    }

    public boolean isShowBeforAfterDays() {
        return showBeforAfterDays;
    }

    public void setShowBeforAfterDays(boolean showBeforAfterDays) {
        this.showBeforAfterDays = showBeforAfterDays;
        refreshView();
    }

    public List<DayMonthYear> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<DayMonthYear> selectedDays) {
        this.selectedDays.clear();
        this.selectedDays.addAll(selectedDays);
        refreshView();
    }

    protected void init() {
        setStretchAllColumns(true);

        initDefaultDate();
        refreshView();
    }

    protected void initDefaultDate() {
        Calendar currDate = Calendar.getInstance();
        year = currDate.get(Calendar.YEAR);
        month = currDate.get(Calendar.MONTH);
    }

    protected void refreshView() {
        Calendar date = Calendar.getInstance();
        date.setFirstDayOfWeek(RACContext.isFirstDaySunday() ? Calendar.SUNDAY : Calendar.MONDAY);
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, 1);
        RACUtil.clearTime(date);

        removeAllViews();

        addWeekHeader();

        int maxWeekOfMonth = date.getActualMaximum(Calendar.WEEK_OF_MONTH);

        if (RACContext.isFirstDaySunday()) {
            while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                date.add(Calendar.DAY_OF_MONTH, -1);
            }
        } else {
            while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                date.add(Calendar.DAY_OF_MONTH, -1);
            }
        }

        for (int i = 0; i < maxWeekOfMonth; i++) {
            TableRow row = newTableRow();

            // add week of the year widget of current row
            TextView weekOfYearText = newTextViewDim();
            weekOfYearText.setText(String.valueOf(date.get(Calendar.WEEK_OF_YEAR)));
            if (!showWeekOfYear) {
                weekOfYearText.setVisibility(View.GONE);
            }
            row.addView(weekOfYearText);

            // add day of the month widgets of current row
            for (int j = 0; j < 7; j++) {
                DayMonthYear dayMonthYear = new DayMonthYear(
                        date.get(Calendar.DAY_OF_MONTH),
                        date.get(Calendar.MONTH),
                        date.get(Calendar.YEAR));

                TextView dayOfMonthText = null;
                if (dayMonthYear.month == month) {
                    dayOfMonthText = newTextView();
                } else {
                    dayOfMonthText = newTextViewDim();
                    if (!showBeforAfterDays) {
                        dayOfMonthText.setVisibility(View.INVISIBLE);
                    }
                }

                dayOfMonthText.setText(String.valueOf(dayMonthYear.day));
                dayOfMonthText.setTag(dayMonthYear);

                dayOfMonthText.setClickable(true);
                if (selectedDays.contains(dayMonthYear)) {
                    dayOfMonthText.setSelected(true);
                    dayOfMonthText.setBackgroundResource(R.color.textBgSelected);
                } else {
                    dayOfMonthText.setSelected(false);
                    dayOfMonthText.setBackgroundResource(R.color.textBg);
                }

                dayOfMonthText.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        TextView textView = (TextView) view;
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (textView.isSelected()) {
                                textView.setBackgroundResource(R.color.textBg);
                            } else {
                                textView.setBackgroundResource(R.color.textBgSelected);
                            }
                        } else {
                            if (textView.isSelected()) {
                                textView.setBackgroundResource(R.color.textBgSelected);
                            } else {
                                textView.setBackgroundResource(R.color.textBg);
                            }
                        }
                        return false;
                    }
                });
                dayOfMonthText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView = (TextView) view;
                        textView.setSelected(!textView.isSelected());
                        if (textView.isSelected()) {
                            textView.setBackgroundResource(R.color.textBgSelected);
                            selectedDays.add((DayMonthYear) textView.getTag());
                        } else {
                            textView.setBackgroundResource(R.color.textBg);
                            selectedDays.remove((DayMonthYear) textView.getTag());
                        }
                    }
                });

                row.addView(dayOfMonthText);
                date.add(Calendar.DAY_OF_MONTH, 1);
            }

            addView(row);
        }
    }

    protected void addWeekHeader() {
        TableRow row = newTableRow();

        if (showWeekOfYear) {
            TextView emptyText = newTextViewDim();
            row.addView(emptyText);
        }

        if (RACContext.isFirstDaySunday()) {
            for (int i = 0; i < 7; i++) {
                TextView dayOfWeekText = newTextViewDim();
                dayOfWeekText.setText(getResources().getStringArray(R.array.dayOfWeekNameHeader)[i]);
                row.addView(dayOfWeekText);
            }
        } else {
            for (int i = 1; i < 8; i++) {
                TextView dayOfWeekText = newTextViewDim();
                dayOfWeekText.setText(getResources().getStringArray(R.array.dayOfWeekNameHeader)[i % 7]);
                row.addView(dayOfWeekText);
            }
        }

        if (!showWeek) {
            row.setVisibility(View.GONE);
        }

        addView(row);
    }

    protected TableRow newTableRow() {
        return (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.calendar_row_template, null);
    }

    protected TextView newTextView() {
        return (TextView) LayoutInflater.from(getContext()).inflate(R.layout.calendar_text_template, null);
    }

    protected TextView newTextViewDim() {
        return (TextView) LayoutInflater.from(getContext()).inflate(R.layout.calendar_text_dim_template, null);
    }

}
