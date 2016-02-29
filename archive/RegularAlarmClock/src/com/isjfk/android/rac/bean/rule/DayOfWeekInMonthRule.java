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
import com.isjfk.android.util.JavaUtil;

/**
 * 每月正数第X周的星期Y日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayOfWeekInMonthRule extends DateRule {

    /** 构造方法 */
    public DayOfWeekInMonthRule() {
        super.setRuleType(RuleTypeEnum.DayOfWeekInMonth);
        super.setRule("1|2,3,4,5,6");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayOfWeekInMonth;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayOfWeekInMonth);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        return res.getString(
                R.string.ruleDescDayOfWeekInMonth,
                RuleDesc.getOrder(res, getOrderInMonth()),
                RuleDesc.dayOfWeekList2Str(res, getDayOfWeekList()));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        return ((getOrderInMonth() == RuleUtil.getOrderInMonth(date))
                && getDayOfWeekList().contains(date.get(Calendar.DAY_OF_WEEK)));
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
     * 获取每月的第几个。
     *
     * @return 每月的第几个，1表示每月第1个
     */
    public Integer getOrderInMonth() {
        return JavaUtil.toInteger(RuleUtil.splitPart(getRule()).get(0));
    }

    /**
     * 设置每月的第几个。
     *
     * @param orderInMonth 每月的第几个，1表示每月第1个
     */
    public void setOrderInMonth(Integer orderInMonth) {
        List<String> rulePartList = RuleUtil.splitPart(getRule());
        rulePartList.set(0, String.valueOf(orderInMonth));
        setRule(RuleUtil.concatPart(rulePartList));
    }

    /**
     * 获取数字表示的星期列表。
     *
     * @return 数字表示的星期列表
     */
    public List<Integer> getDayOfWeekList() {
        return RuleUtil.toDayOfWeekList(RuleUtil.splitPart(getRule()).get(1));
    }

    /**
     * 设置星期列表。
     *
     * @param dayOfWeekList 数字表示的星期列表
     */
    public void setDayOfWeekList(List<Integer> dayOfWeekList) {
        List<String> rulePartList = RuleUtil.splitPart(getRule());
        rulePartList.set(1, RuleUtil.toDayOfWeekStr(dayOfWeekList));
        setRule(RuleUtil.concatPart(rulePartList));
    }

}
