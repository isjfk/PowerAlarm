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
import com.isjfk.android.rac.bean.other.DayMonth;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.common.RACConstant;
import com.isjfk.android.rac.common.RACTimeDesc;
import com.isjfk.android.rac.rule.RuleUtil;

/**
 * 指定月日范围日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayMonthRangeRule extends DateRule {

    /** 构造方法 */
    public DayMonthRangeRule() {
        super.setRuleType(RuleTypeEnum.DayMonthRange);

        Calendar beginDate = Calendar.getInstance();
        Calendar endDate = (Calendar) beginDate.clone();
        String beginDay = String.valueOf(beginDate.get(Calendar.DAY_OF_MONTH));
        String beginMonth = String.valueOf(beginDate.get(Calendar.MONTH));
        String endDay = String.valueOf(endDate.get(Calendar.DAY_OF_MONTH));
        String endMonth = String.valueOf(endDate.get(Calendar.MONTH));
        super.setRule(
                beginDay + "." + beginMonth
                + "|"
                + endDay + "." + endMonth);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayMonthRange;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayMonthRange);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        DayMonth startDayMonth = getStartDayMonth();
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        startTime.set(Calendar.MONTH, startDayMonth.month);
        startTime.set(Calendar.DAY_OF_MONTH, startDayMonth.day);

        DayMonth endDayMonth = getEndDayMonth();
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.YEAR, RACConstant.DEFAULT_LEAP_YEAR);
        endTime.set(Calendar.MONTH, endDayMonth.month);
        endTime.set(Calendar.DAY_OF_MONTH, endDayMonth.day);

        return res.getString(
                R.string.ruleDescDayMonthRange,
                RACTimeDesc.descDayMonthShort(startTime),
                RACTimeDesc.descDayMonthShort(endTime));
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        DayMonth dateDayMonth = new DayMonth(date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH));

        DayMonth startDayMonth = getStartDayMonth();
        DayMonth endDayMonth = getEndDayMonth();

        if (startDayMonth.compareTo(endDayMonth) <= 0) {
            return (dateDayMonth.compareTo(startDayMonth) >= 0) && (dateDayMonth.compareTo(endDayMonth) <=0);
        } else {
            return (dateDayMonth.compareTo(startDayMonth) >= 0) || (dateDayMonth.compareTo(endDayMonth) <=0);
        }
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
     * 获取起始月日。
     *
     * @return 起始月日
     */
    public DayMonth getStartDayMonth() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toDayMonth(partList.get(0));
    }

    /**
     * 设置起始月日。
     *
     * @param dayMonth 起始月日
     */
    public void setStartDayMonth(DayMonth dayMonth) {
        List<String> partList = RuleUtil.splitPart(getRule());
        partList.set(0, RuleUtil.toDayMonthStr(dayMonth));
        setRule(RuleUtil.concatPart(partList));
    }

    /**
     * 获取结束月日。
     *
     * @return 结束月日
     */
    public DayMonth getEndDayMonth() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toDayMonth(partList.get(1));
    }

    /**
     * 设置结束月日。
     *
     * @param dayMonth 结束月日
     */
    public void setEndDayMonth(DayMonth dayMonth) {
        List<String> partList = RuleUtil.splitPart(getRule());
        partList.set(1, RuleUtil.toDayMonthStr(dayMonth));
        setRule(RuleUtil.concatPart(partList));
    }

}
