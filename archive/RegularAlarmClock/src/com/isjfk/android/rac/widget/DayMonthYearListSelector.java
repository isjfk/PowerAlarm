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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.widget.DayMonthYearPicker.OnDayMonthYearChangedListener;

/**
 * 指定日期选择控件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-7
 */
public class DayMonthYearListSelector extends LinearLayout {

    protected LayoutInflater inflater;

    protected List<DayMonthYear> dayMonthYearList = new ArrayList<DayMonthYear>();
    protected View footer = null;

    protected DayMonthYearListChangedListener footerListener = null;
    protected List<DayMonthYearListChangedListener> dayMonthYearListChangedListeners = new ArrayList<DayMonthYearListChangedListener>();

    public DayMonthYearListSelector(Context context) {
        super(context);
        init(context);
    }

    public DayMonthYearListSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        this.inflater = LayoutInflater.from(context);

        setFooter(inflater);

        ImageButton addButton = (ImageButton) footer.findViewById(R.id.dayMonthYearListItemAddButton);
        addButton.setOnClickListener(new AddDayMonthYearListener());
    }

    /**
     * 获取指定日期列表。
     *
     * @return 指定日期列表
     */
    public List<DayMonthYear> getDayMonthYearList() {
        return new ArrayList<DayMonthYear>(dayMonthYearList);
    }

    /**
     * 批量设置指定日期。
     *
     * @param dayMonthYearList 指定日期列表
     */
    public void setDayMonthYearList(List<DayMonthYear> dayMonthYearList) {
        removeAllDayMonthYearNoEvent();
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            addDayMonthYearNoEvent(-1, dayMonthYear);
        }

        fireDayMonthYearListChangedEvent();
    }

    /**
     * 获取日期总数。
     *
     * @return 日期总数
     */
    public int getDayMonthYearCount() {
        return dayMonthYearList.size();
    }

    /**
     * 获取指定位置的日期。
     *
     * @param position 位置
     * @return 日期
     */
    public DayMonthYear getDayMonthYear(int position) {
        return dayMonthYearList.get(position);
    }

    /**
     * 获取指定日期的位置。
     *
     * @param dayMonthYear 日期
     * @return 日期的位置
     */
    public int posOfDayMonthYear(DayMonthYear dayMonthYear) {
        return dayMonthYearList.indexOf(dayMonthYear);
    }

    /**
     * 增加日期。
     *
     * @param dayMonthYear 日期
     */
    public void addDayMonthYear(DayMonthYear dayMonthYear) {
        addDayMonthYearNoEvent(-1, dayMonthYear);
        fireDayMonthYearListChangedEvent();
    }

    /**
     * 在指定的位置增加日期。
     *
     * @param position 位置
     * @param dayMonthYear 日期
     */
    public void addDayMonthYear(int position, DayMonthYear dayMonthYear) {
        addDayMonthYearNoEvent(position, dayMonthYear);
        fireDayMonthYearListChangedEvent();
    }

    /**
     * 在指定的位置增加日期，不触发事件。
     *
     * @param position 位置，如果小于0则增加到最后
     * @param dayMonthYear 日期
     */
    protected void addDayMonthYearNoEvent(int position, DayMonthYear dayMonthYear) {
        if (position < 0) {
            position = dayMonthYearList.size();
        }
        dayMonthYearList.add(position, dayMonthYear);
        addView(newDayMonthYearView(position), position);
    }

    /**
     * 删除指定位置的日期。
     *
     * @param position 位置
     */
    public void removeDayMonthYear(int position) {
        removeDayMonthYearNoEvent(position);
        fireDayMonthYearListChangedEvent();
    }

    /**
     * 删除指定位置的日期，不触发事件。
     *
     * @param position 位置
     */
    protected void removeDayMonthYearNoEvent(int position) {
        dayMonthYearList.remove(position);
        removeViewAt(position);
    }

    /**
     * 删除指定日期。
     *
     * @param dayMonthYear 日期
     * @return 被删除的日期位置，如果找不到返回-1
     */
    public int removeDayMonthYear(DayMonthYear dayMonthYear) {
        int pos = removeDayMonthYearNoEvent(dayMonthYear);
        fireDayMonthYearListChangedEvent();
        return pos;
    }

    /**
     * 删除指定日期。
     *
     * @param dayMonthYear 日期
     * @return 被删除的日期位置，如果找不到返回-1
     */
    protected int removeDayMonthYearNoEvent(DayMonthYear dayMonthYear) {
        int pos = posOfDayMonthYear(dayMonthYear);
        if (pos != -1) {
            removeDayMonthYearNoEvent(pos);
        }
        return pos;
    }

    /**
     * 将原有的日期替换为新的日期。
     *
     * @param orgDayMonthYear 原有的日期
     * @param newDayMonthYear 新日期
     * @return 被替换的日期位置，如果找不到返回-1
     */
    public int replaceDayMonthYear(DayMonthYear orgDayMonthYear, DayMonthYear newDayMonthYear) {
        int pos = removeDayMonthYearNoEvent(orgDayMonthYear);
        if (pos != -1) {
            addDayMonthYearNoEvent(pos, newDayMonthYear);
        }

        fireDayMonthYearListChangedEvent();
        return pos;
    }

    /**
     * 删除所有日期。
     */
    public void removeAllDayMonthYear() {
        removeAllDayMonthYearNoEvent();
        fireDayMonthYearListChangedEvent();
    }

    /**
     * 删除所有日期，不触发事件。
     */
    protected void removeAllDayMonthYearNoEvent() {
        while (dayMonthYearList.size() != 0) {
            removeDayMonthYearNoEvent(dayMonthYearList.size() - 1);
        }
    }

    /**
     * 增加日期变更事件监听器。
     *
     * @param listener 日期变更事件监听器
     */
    public void addDayMonthYearListChangedListener(DayMonthYearListChangedListener listener) {
        dayMonthYearListChangedListeners.add(listener);
    }

    /**
     * 删除日期变更事件监听器。
     *
     * @param listener 日期变更事件监听器
     */
    public void removeDayMonthYearListChangedListener(DayMonthYearListChangedListener listener) {
        dayMonthYearListChangedListeners.remove(listener);
    }

    /**
     * 触发日期变更事件。
     */
    protected void fireDayMonthYearListChangedEvent() {
        List<DayMonthYear> dayMonthYearList = getDayMonthYearList();
        List<DayMonthYearListChangedListener> listenerList =
                new ArrayList<DayMonthYearListChangedListener>(dayMonthYearListChangedListeners);
        for (DayMonthYearListChangedListener listener : listenerList) {
            listener.onDayMonthYearListChanged(this, dayMonthYearList);
        }

        if (dayMonthYearList.size() <= 1) {
            disableAllDeleteButtons();
        } else {
            enableAllDeleteButtons();
        }
    }

    /**
     * 隐藏所有的删除按钮。
     */
    protected void disableAllDeleteButtons() {
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            View dayMonthYearView = getDayMonthYearView(dayMonthYear);
            ImageButton delButton = (ImageButton) dayMonthYearView.findViewById(R.id.dayMonthYearListItemDelButton);
            delButton.setEnabled(false);
        }
    }

    /**
     * 显示所有的删除按钮。
     */
    protected void enableAllDeleteButtons() {
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            View dayMonthYearView = getDayMonthYearView(dayMonthYear);
            ImageButton delButton = (ImageButton) dayMonthYearView.findViewById(R.id.dayMonthYearListItemDelButton);
            delButton.setEnabled(true);
        }
    }

    /**
     * 生成指定位置日期使用的控件。
     *
     * @param position 规则位置
     * @return 指定位置规则使用的控件
     */
    protected View newDayMonthYearView(int position) {
        View dayMonthYearView = newDayMonthYearView();
        bindDayMonthYearView(position, dayMonthYearView, getDayMonthYear(position));
        return dayMonthYearView;
    }

    /**
     * 生成规则使用的显示控件。
     *
     * @return 规则使用的显示控件
     */
    protected View newDayMonthYearView() {
        return inflater.inflate(R.layout.daymonthyear_list_item, null);
    }

    /**
     * 将日期数据绑定到控件上。
     *
     * @param position 日期数据索引
     * @param dayMonthYearView 日期控件
     * @param dayMonthYear 日期数据
     */
    protected void bindDayMonthYearView(int position, View dayMonthYearView, DayMonthYear dayMonthYear) {
        ImageButton delButton = (ImageButton) dayMonthYearView.findViewById(R.id.dayMonthYearListItemDelButton);
        delButton.setOnClickListener(new DeleteDayMonthYearListener(dayMonthYear));

        DayMonthYearPicker dayMonthYearPicker = (DayMonthYearPicker) dayMonthYearView.findViewById(R.id.dayMonthYearListItemPicker);
        dayMonthYearPicker.setDayMonthYear(dayMonthYear.day, dayMonthYear.month, dayMonthYear.year);
        dayMonthYearPicker.setOnDayMonthYearChangedListener(new ChangeDayMonthYearListener(dayMonthYear));
    }

    /**
     * 获取指定位置的日期控件。
     *
     * @param position 日期控件位置
     * @return 日期控件，如果位置不存在返回null
     */
    protected View getDayMonthYearView(int position) {
        return getChildAt(position);
    }

    /**
     * 获取指定位置日期对应的显示控件。
     *
     * @param dayMontyYear 日期
     * @return 日期对应的显示控件
     */
    protected View getDayMonthYearView(DayMonthYear dayMonthYear) {
        int pos = posOfDayMonthYear(dayMonthYear);
        if (pos != -1) {
            return getDayMonthYearView(pos);
        }
        return null;
    }

    /**
     * 设置列表底部控件。
     *
     * @param inflater 控件构造器
     */
    protected void setFooter(LayoutInflater inflater) {
        footer = inflater.inflate(R.layout.daymonthyear_list_footer, null);
        addView(footer);
    }

    /**
     * 新增日期事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2013-5-15
     */
    protected class AddDayMonthYearListener implements OnClickListener {
        /**
         * {@inheritDoc}
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            Calendar cal = Calendar.getInstance();

            if (!dayMonthYearList.isEmpty()) {
                DayMonthYear lastDayMonthYear = dayMonthYearList.get(dayMonthYearList.size() - 1);
                cal.set(Calendar.YEAR, lastDayMonthYear.year);
                cal.set(Calendar.MONTH, lastDayMonthYear.month);
                cal.set(Calendar.DAY_OF_MONTH, lastDayMonthYear.day);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            addDayMonthYear(new DayMonthYear(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)));
        }
    }

    /**
     * 删除日期事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2013-5-15
     */
    protected class DeleteDayMonthYearListener implements OnClickListener {
        protected DayMonthYear dayMonthYear;

        public DeleteDayMonthYearListener(DayMonthYear dayMonthYear) {
            this.dayMonthYear = dayMonthYear;
        }

        /**
         * {@inheritDoc}
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            removeDayMonthYear(dayMonthYear);
        }
    }

    /**
     * 修改日期事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2013-5-15
     */
    protected class ChangeDayMonthYearListener implements OnDayMonthYearChangedListener {
        protected DayMonthYear dayMonthYear;

        public ChangeDayMonthYearListener(DayMonthYear dayMonthYear) {
            this.dayMonthYear = dayMonthYear;
        }

        /**
         * {@inheritDoc}
         * @see com.isjfk.android.rac.widget.DayMonthYearPicker.OnDayMonthYearChangedListener#onDayMonthYearChanged(com.isjfk.android.rac.widget.DayMonthYearPicker)
         */
        @Override
        public void onDayMonthYearChanged(DayMonthYearPicker picker) {
            dayMonthYear.day = picker.getDay();
            dayMonthYear.month = picker.getMonth();
            dayMonthYear.year = picker.getYear();
        }
    }

    /**
     * 指定日期变更事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-14
     */
    public static interface DayMonthYearListChangedListener {

        /**
         * 指定日期变更事件。
         *
         * @param view 发生变更的控件
         * @param dayMonthYearList 变更后的指定日期列表
         */
        public void onDayMonthYearListChanged(DayMonthYearListSelector view, List<DayMonthYear> dayMonthYearList);

    }

}
