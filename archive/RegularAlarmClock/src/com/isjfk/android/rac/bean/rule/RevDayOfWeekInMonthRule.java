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
 * 每月倒数第X周的星期Y日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class RevDayOfWeekInMonthRule extends DateRule {

    /** 构造方法 */
    public RevDayOfWeekInMonthRule() {
        super.setRuleType(RuleTypeEnum.RevDayOfWeekInMonth);
        super.setRule("1|7");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.RevDayOfWeekInMonth;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.RevDayOfWeekInMonth);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        int revWeekInMonth = getRevOrderInMonth();
        if (revWeekInMonth == 1) {
            return res.getString(
                    R.string.ruleDescDayOfLastWeekInMonth,
                    RuleDesc.dayOfWeekList2Str(res, getDayOfWeekList()));
        }
        return res.getString(
                R.string.ruleDescRevDayOfWeekInMonth,
                RuleDesc.getOrder(res, revWeekInMonth),
                RuleDesc.dayOfWeekList2Str(res, getDayOfWeekList()));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        return ((getRevOrderInMonth() == RuleUtil.getRevOrderInMonth(date))
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
     * 获取每月的倒数第几个。
     *
     * @return 每月的倒数第几个，1表示每月倒数第1个
     */
    public Integer getRevOrderInMonth() {
        return JavaUtil.toInteger(RuleUtil.splitPart(getRule()).get(0));
    }

    /**
     * 设置每月的倒数第几个。
     *
     * @param orderInMonth 每月的倒数第几个，1表示每月倒数第1个
     */
    public void setRevOrderInMonth(Integer orderInMonth) {
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
