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
 * 响铃X天停止Y天日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-12-23
 */
public class AlarmXOffYDaysRule extends DateRule {

    /** 构造方法 */
    public AlarmXOffYDaysRule() {
        super.setRuleType(RuleTypeEnum.AlarmXOffYDays);

        Calendar date = Calendar.getInstance();
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(date.get(Calendar.MONTH));
        String year = String.valueOf(date.get(Calendar.YEAR));
        super.setRule(day + "." + month + "." + year + "|21,7");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.AlarmXOffYDays;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.AlarmXOffYDays);
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

        return res.getString(
                R.string.ruleDescAlarmXOffYDays,
                RACTimeDesc.descDateShort(startTime),
                getAlarmDays(),
                getOffDays());
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

        if (date.compareTo(start) < 0) {
            return false;
        }

        // 计算日期间相差天数
        int days = 0;
        if (start.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            days = date.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        } else {
            days = start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR) + 1;
            start.add(Calendar.DAY_OF_YEAR, days);
            while (start.get(Calendar.YEAR) < date.get(Calendar.YEAR)) {
                days += start.getActualMaximum(Calendar.DAY_OF_YEAR);
                start.add(Calendar.YEAR, 1);
            }
            days += date.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        }

        int alarmDays = getAlarmDays();
        int offDays = getOffDays();

        // 相差天数除以周期长度取余数，如果小于响铃天数就表示需要响铃
        int recentAlarmedDays = days % (alarmDays + offDays);
        return recentAlarmedDays < alarmDays;
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
     * 获取开始响铃的年月日。
     *
     * @return 开始响铃的年月日
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
     * 获取响铃天数。
     *
     * @return 响铃天数
     */
    public Integer getAlarmDays() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toIntList(partList.get(1)).get(0);
    }

    /**
     * 设置响铃天数。
     *
     * @param alarmDays 响铃天数
     */
    public void setAlarmDays(Integer alarmDays) {
        List<String> partList = RuleUtil.splitPart(getRule());
        List<Integer> intList = RuleUtil.toIntList(partList.get(1));
        intList.set(0, alarmDays);
        partList.set(1, RuleUtil.toCommaSepIntStr(intList));
        setRule(RuleUtil.concatPart(partList));
    }

    /**
     * 获取停止天数。
     *
     * @return 停止天数
     */
    public Integer getOffDays() {
        List<String> partList = RuleUtil.splitPart(getRule());
        return RuleUtil.toIntList(partList.get(1)).get(1);
    }

    /**
     * 设置停止天数。
     *
     * @param offDays 停止天数
     */
    public void setOffDays(Integer offDays) {
        List<String> partList = RuleUtil.splitPart(getRule());
        List<Integer> intList = RuleUtil.toIntList(partList.get(1));
        intList.set(1, offDays);
        partList.set(1, RuleUtil.toCommaSepIntStr(intList));
        setRule(RuleUtil.concatPart(partList));
    }

}
