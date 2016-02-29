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
import com.isjfk.android.rac.common.RACTimeDesc;
import com.isjfk.android.rac.rule.RuleUtil;

/**
 * 指定日期范围日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayMonthYearRangeRule extends DateRule {

    /** 构造方法 */
    public DayMonthYearRangeRule() {
        super.setRuleType(RuleTypeEnum.DayMonthYearRange);

        Calendar beginDate = Calendar.getInstance();
        Calendar endDate = (Calendar) beginDate.clone();
        String beginDay = String.valueOf(beginDate.get(Calendar.DAY_OF_MONTH));
        String beginMonth = String.valueOf(beginDate.get(Calendar.MONTH));
        String beginYear = String.valueOf(beginDate.get(Calendar.YEAR));
        String endDay = String.valueOf(endDate.get(Calendar.DAY_OF_MONTH));
        String endMonth = String.valueOf(endDate.get(Calendar.MONTH));
        String endYear = String.valueOf(endDate.get(Calendar.YEAR));
        super.setRule(
                beginDay + "." + beginMonth + "." + beginYear
                + "|"
                + endDay + "." + endMonth + "." + endYear);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayMonthYearRange;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayMonthYearRange);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        DayMonthYear startDayMonthYear = getStartDayMonthYear();
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, startDayMonthYear.year);
        startTime.set(Calendar.MONTH, startDayMonthYear.month);
        startTime.set(Calendar.DAY_OF_MONTH, startDayMonthYear.day);

        DayMonthYear endDayMonthYear = getEndDayMonthYear();
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.YEAR, endDayMonthYear.year);
        endTime.set(Calendar.MONTH, endDayMonthYear.month);
        endTime.set(Calendar.DAY_OF_MONTH, endDayMonthYear.day);

        return res.getString(
                R.string.ruleDescDayMonthYearRange,
                RACTimeDesc.descDateShort(startTime),
                RACTimeDesc.descDateShort(endTime));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        DayMonthYear startDayMonthYear = getStartDayMonthYear();
        Calendar start = (Calendar) date.clone();
        start.set(Calendar.YEAR, startDayMonthYear.year);
        start.set(Calendar.MONTH, startDayMonthYear.month);
        start.set(Calendar.DAY_OF_MONTH, startDayMonthYear.day);

        DayMonthYear endDayMonthYear = getEndDayMonthYear();
        Calendar end = (Calendar) date.clone();
        end.set(Calendar.YEAR, endDayMonthYear.year);
        end.set(Calendar.MONTH, endDayMonthYear.month);
        end.set(Calendar.DAY_OF_MONTH, endDayMonthYear.day);

        return (date.compareTo(start) >= 0) && (date.compareTo(end) <= 0);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getExpireDate()
     */
    @Override
    public DayMonthYear getExpireDate() {
        return getEndDayMonthYear();
    }

    /**
     * 获取起始年月日。
     *
     * @return 起始年月日
     */
    public DayMonthYear getStartDayMonthYear() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toDayMonthYear(partList.get(0));
    }

    /**
     * 设置起始年月日。
     *
     * @param dayMonthYear 起始年月日
     */
    public void setStartDayMonthYear(DayMonthYear dayMonthYear) {
        List<String> partList = RuleUtil.splitPart(getRule());
        partList.set(0, RuleUtil.toDayMonthYearStr(dayMonthYear));
        setRule(RuleUtil.concatPart(partList));
    }

    /**
     * 获取结束年月日。
     *
     * @return 结束年月日
     */
    public DayMonthYear getEndDayMonthYear() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toDayMonthYear(partList.get(1));
    }

    /**
     * 设置结束年月日。
     *
     * @param dayMonthYear 结束年月日
     */
    public void setEndDayMonthYear(DayMonthYear dayMonthYear) {
        List<String> partList = RuleUtil.splitPart(getRule());
        partList.set(1, RuleUtil.toDayMonthYearStr(dayMonthYear));
        setRule(RuleUtil.concatPart(partList));
    }

}
