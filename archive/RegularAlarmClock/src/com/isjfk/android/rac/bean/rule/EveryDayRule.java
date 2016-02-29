/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.rule;

import java.util.Calendar;

import android.content.res.Resources;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.bean.other.DayMonthYear;

/**
 * 每天生效的日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class EveryDayRule extends DateRule {

    /** 构造方法 */
    public EveryDayRule() {
        super.setRuleType(RuleTypeEnum.EveryDay);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.EveryDay;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.EveryDay);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        return res.getString(R.string.ruleDescEveryday);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        return true;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getExpireDate()
     */
    @Override
    public DayMonthYear getExpireDate() {
        return null;
    }

}
