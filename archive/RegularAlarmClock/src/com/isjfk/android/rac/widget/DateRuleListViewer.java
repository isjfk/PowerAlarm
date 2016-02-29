/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.EModeEnum;
import com.isjfk.android.rac.rule.RuleUtil;

/**
 * 日期规则查看控件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-7
 */
public class DateRuleListViewer extends LinearLayout {

    protected LayoutInflater inflater;
    protected String[] eModes;

    protected List<DateRule> dateRuleList = new ArrayList<DateRule>();
    protected View footer = null;

    protected DateRuleChangedListener footerListener = null;
    protected List<DateRuleChangedListener> dateRuleChangedListeners = new ArrayList<DateRuleChangedListener>();

    public DateRuleListViewer(Context context) {
        super(context);
        init(context);
    }

    public DateRuleListViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        this.inflater = LayoutInflater.from(context);
        eModes = getResources().getStringArray(R.array.dateRuleEMode);

        setFooter(inflater);
    }

    /**
     * 获取所有日期规则。
     *
     * @return 所有日期规则
     */
    public List<DateRule> getDateRuleList() {
        if (!dateRuleList.isEmpty()) {
            dateRuleList.get(0).setEMode(EModeEnum.ADD);
        }
        return new ArrayList<DateRule>(dateRuleList);
    }

    /**
     * 批量设置日期规则。
     *
     * @param dateRuleList 日期规则列表
     */
    public void setDateRuleList(List<DateRule> dateRuleList) {
        removeAllDateRuleNoEvent();
        for (DateRule dateRule : dateRuleList) {
            addDateRuleNoEvent(-1, dateRule);
        }

        fireDateRuleChangedEvent();
    }

    /**
     * 获取日期规则总数。
     *
     * @return 日期规则总数
     */
    public int getDateRuleCount() {
        return dateRuleList.size();
    }

    /**
     * 获取指定位置的日期规则。
     *
     * @param position 位置
     * @return 日期规则
     */
    public DateRule getDateRule(int position) {
        return dateRuleList.get(position);
    }

    /**
     * 获取日期规则的位置。
     *
     * @param dateRule 日期规则
     * @return 日期规则的位置
     */
    public int posOfDateRule(DateRule dateRule) {
        return dateRuleList.indexOf(dateRule);
    }

    /**
     * 增加日期规则。
     *
     * @param dateRule 日期规则
     */
    public void addDateRule(DateRule dateRule) {
        addDateRuleNoEvent(-1, dateRule);
        fireDateRuleChangedEvent();
    }

    /**
     * 在指定的位置增加日期规则。
     *
     * @param position 位置
     * @param dateRule 日期规则
     */
    public void addDateRule(int position, DateRule dateRule) {
        addDateRuleNoEvent(position, dateRule);
        fireDateRuleChangedEvent();
    }

    /**
     * 在指定的位置增加日期规则，不触发事件。
     *
     * @param position 位置，如果小于0则增加到最后
     * @param dateRule 日期规则
     */
    protected void addDateRuleNoEvent(int position, DateRule dateRule) {
        if (position < 0) {
            position = dateRuleList.size();
        }
        dateRuleList.add(position, dateRule);
        addView(newDateRuleView(position), position);

        // 因为第一个日期规则不显示生效方式，所以如果新增的规则在最开始且把原来原有的日期规则挤到第二个
        // 应该重设第二个日期规则以显示生效方式
        if ((position == 0) && (getDateRuleCount() > 1)) {
            resetDateRuleView(1);
        }
    }

    /**
     * 删除指定位置的日期规则。
     *
     * @param position 位置
     */
    public void removeDateRule(int position) {
        removeDateRuleNoEvent(position);
        fireDateRuleChangedEvent();
    }

    /**
     * 删除指定位置的日期规则，不触发事件。
     *
     * @param position 位置
     */
    protected void removeDateRuleNoEvent(int position) {
        dateRuleList.remove(position);
        removeViewAt(position);

        if ((position == 0) && (getDateRuleCount() > 0)) {
            resetDateRuleView(0);
        }
    }

    /**
     * 删除日期规则。
     *
     * @param dateRule 日期规则
     * @return 被删除的日期规则位置，如果找不到返回-1
     */
    public int removeDateRule(DateRule dateRule) {
        int pos = removeDateRuleNoEvent(dateRule);
        fireDateRuleChangedEvent();
        return pos;
    }

    /**
     * 删除日期规则。
     *
     * @param dateRule 日期规则
     * @return 被删除的日期规则位置，如果找不到返回-1
     */
    protected int removeDateRuleNoEvent(DateRule dateRule) {
        int pos = posOfDateRule(dateRule);
        if (pos != -1) {
            removeDateRuleNoEvent(pos);
        }
        return pos;
    }

    /**
     * 将原有的日期规则替换为新的日期规则。
     *
     * @param orgDateRule 原有的日期规则
     * @param newDateRule 新日期规则
     * @return 被替换的日期规则位置，如果找不到返回-1
     */
    public int replaceDateRule(DateRule orgDateRule, DateRule newDateRule) {
        int pos = removeDateRuleNoEvent(orgDateRule);
        if (pos != -1) {
            addDateRuleNoEvent(pos, newDateRule);
        }

        fireDateRuleChangedEvent();
        return pos;
    }

    /**
     * 删除所有日期规则。
     */
    public void removeAllDateRule() {
        removeAllDateRuleNoEvent();
        fireDateRuleChangedEvent();
    }

    /**
     * 删除所有日期规则，不触发事件。
     */
    protected void removeAllDateRuleNoEvent() {
        while (dateRuleList.size() != 0) {
            removeDateRuleNoEvent(dateRuleList.size() - 1);
        }
    }

    /**
     * 增加日期规则变更事件监听器。
     *
     * @param listener 日期规则变更事件监听器
     */
    public void addDateRuleChangedListener(DateRuleChangedListener listener) {
        dateRuleChangedListeners.add(listener);
    }

    /**
     * 删除日期规则变更事件监听器。
     *
     * @param listener 日期规则变更事件监听器
     */
    public void removeDateRuleChangedListener(DateRuleChangedListener listener) {
        dateRuleChangedListeners.remove(listener);
    }

    /**
     * 触发日期规则变更事件。
     */
    protected void fireDateRuleChangedEvent() {
        List<DateRule> dateRuleList = getDateRuleList();
        for (DateRuleChangedListener listener : new ArrayList<DateRuleChangedListener>(dateRuleChangedListeners)) {
            listener.onDateRuleChanged(this, dateRuleList);
        }

        // 触发底部控件事件
        if (footerListener != null) {
            footerListener.onDateRuleChanged(this, dateRuleList);
        }
    }

    /**
     * 生成指定位置规则使用的控件。
     *
     * @param position 规则位置
     * @return 指定位置规则使用的控件
     */
    protected View newDateRuleView(int position) {
        View dateRuleView = newDateRuleView();
        bindDateRuleView(position, dateRuleView, getDateRule(position));
        return dateRuleView;
    }

    /**
     * 生成规则使用的显示控件。
     *
     * @return 规则使用的显示控件
     */
    protected View newDateRuleView() {
        return inflater.inflate(R.layout.daterule_view_list_item, null);
    }

    /**
     * 将日期规则数据绑定到控件上。
     *
     * @param position 日期规则数据索引
     * @param dateRuleView 日期规则控件
     * @param dateRule 日期规则数据
     */
    protected void bindDateRuleView(int position, View dateRuleView, DateRule dateRule) {
        TextView eModeText = (TextView) dateRuleView.findViewById(R.id.dateRuleEMode);
        TextView descText = (TextView) dateRuleView.findViewById(R.id.dateRuleDesc);

        eModeText.setText(eModes[dateRule.getEMode()]);
        descText.setText(dateRule.desc(getResources()));

        if (position == 0) {
            eModeText.setVisibility(View.GONE);
        } else {
            eModeText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重设日期规则控件的显示状态。
     *
     * @param dateRule 需要重设的日期规则控件
     */
    protected void resetDateRuleView(DateRule dateRule) {
        int pos = posOfDateRule(dateRule);
        if (pos != -1) {
            resetDateRuleView(pos);
        }
    }

    /**
     * 重设日期规则控件的显示状态。
     *
     * @param position 需要重设的日期规则控件位置
     */
    protected void resetDateRuleView(int position) {
        bindDateRuleView(position, getDateRuleView(position), getDateRule(position));
    }

    /**
     * 获取指定位置的日期规则控件。
     *
     * @param position 日期规则控件位置
     * @return 日期规则控件，如果位置不存在返回null
     */
    protected View getDateRuleView(int position) {
        return getChildAt(position);
    }

    /**
     * 获取指定位置日期规则对应的显示控件。
     *
     * @param dateRule 日期规则
     * @return 日期规则对应的显示控件
     */
    protected View getDateRuleView(DateRule dateRule) {
        int pos = posOfDateRule(dateRule);
        if (pos != -1) {
            return getDateRuleView(pos);
        }
        return null;
    }

    /**
     * 获取日期规则控件对应的日期规则。
     *
     * @param view 日期规则控件
     * @return 日期规则
     */
    protected DateRule getViewDateRule(View view) {
        return getDateRule(indexOfChild(view));
    }

    /**
     * 设置列表底部控件。
     *
     * @param inflater 控件构造器
     */
    protected void setFooter(LayoutInflater inflater) {
        footer = inflater.inflate(R.layout.daterule_view_list_footer, null);
        addView(footer);

        footerListener = new ShowFooterOnOneTimeDateRuleListener((TextView) footer.findViewById(R.id.dateRuleOneTime));
    }

    /**
     * 日期规则变更事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-14
     */
    public static interface DateRuleChangedListener {

        /**
         * 日期规则变更事件。
         *
         * @param view 发生变更的控件
         * @param dateRuleList 变更后的日期规则列表
         */
        public void onDateRuleChanged(DateRuleListViewer view, List<DateRule> dateRuleList);

    }

    /**
     * 当日期规则列表为空时显示底部控件的事件监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-14
     */
    public static class ShowFooterOnOneTimeDateRuleListener implements DateRuleChangedListener {

        private View footer;

        /**
         * 构造方法。
         *
         * @param footer 底部控件
         */
        public ShowFooterOnOneTimeDateRuleListener(View footer) {
            this.footer = footer;
        }

        /**
         * {@inheritDoc}
         * @see com.isjfk.android.rac.widget.DateRuleListViewer.DateRuleChangedListener#onDateRuleChanged(com.isjfk.android.rac.widget.DateRuleListViewer, java.util.List)
         */
        @Override
        public void onDateRuleChanged(DateRuleListViewer view, List<DateRule> dateRuleList) {
            if (RuleUtil.isOneTime(dateRuleList)) {
                footer.setVisibility(View.VISIBLE);
            } else {
                footer.setVisibility(View.GONE);
            }
        }

    }

}
