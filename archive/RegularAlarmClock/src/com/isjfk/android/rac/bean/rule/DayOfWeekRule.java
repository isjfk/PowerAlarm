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
 * 按周生效的日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayOfWeekRule extends DateRule {

    /** 构造方法 */
    public DayOfWeekRule() {
        super.setRuleType(RuleTypeEnum.DayOfWeek);
        super.setRule("2,3,4,5,6");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayOfWeek;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayOfWeek);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        return res.getString(
                R.string.ruleDescDayOfWeek,
                RuleDesc.dayOfWeekList2Str(res, getDayOfWeekList()));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        return getDayOfWeekList().contains(date.get(Calendar.DAY_OF_WEEK));
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
     * 获取数字表示的星期列表。
     *
     * @return 数字表示的星期列表
     */
    public List<Integer> getDayOfWeekList() {
        return RuleUtil.toDayOfWeekList(getRule());
    }

    /**
     * 设置星期列表。
     *
     * @param dayOfWeekList 数字表示的星期列表
     */
    public void setDayOfWeekList(List<Integer> dayOfWeekList) {
        setRule(RuleUtil.toDayOfWeekStr(dayOfWeekList));
    }

}
