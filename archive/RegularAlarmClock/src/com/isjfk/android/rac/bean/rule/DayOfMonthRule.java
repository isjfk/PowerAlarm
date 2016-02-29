/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.rule;

import java.util.Calendar;
import java.util.List;

import android.content.res.Resources;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.rule.RuleDesc;
import com.isjfk.android.rac.rule.RuleUtil;

/**
 * 按月生效的日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayOfMonthRule extends DateRule {

    /** 构造方法 */
    public DayOfMonthRule() {
        super.setRuleType(RuleTypeEnum.DayOfMonth);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayOfMonth;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayOfMonth);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        return res.getString(
                R.string.ruleDescDayOfMonth,
                RuleDesc.dayOfMonthList2Str(res, getDayOfMonthList()));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        return getDayOfMonthList().contains(date.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getExpireDate()
     */
    @Override
    public DayMonthYear getExpireDate() {
        return null;
    }

    /**
     * 获取数字表示的日期列表。
     *
     * @return 数字表示的日期列表
     */
    public List<Integer> getDayOfMonthList() {
        return RuleUtil.toDayOfMonthList(getRule());
    }

    /**
     * 设置日期列表。
     *
     * @param dayOfMonthList 数字表示的日期列表
     */
    public void setDayOfMonthList(List<Integer> dayOfMonthList) {
        setRule(RuleUtil.toDayOfMonthStr(dayOfMonthList));
    }

}
