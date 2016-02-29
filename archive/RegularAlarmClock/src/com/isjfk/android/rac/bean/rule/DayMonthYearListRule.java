/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean.rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.res.Resources;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.common.RACTimeDesc;
import com.isjfk.android.rac.rule.RuleUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 指定日期规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class DayMonthYearListRule extends DateRule {

    /** 构造方法 */
    public DayMonthYearListRule() {
        super.setRuleType(RuleTypeEnum.DayMonthYearList);

        Calendar currDate = Calendar.getInstance();
        String currDay = String.valueOf(currDate.get(Calendar.DAY_OF_MONTH));
        String currMonth = String.valueOf(currDate.get(Calendar.MONTH));
        String currYear = String.valueOf(currDate.get(Calendar.YEAR));
        super.setRule(
                currDay + "." + currMonth + "." + currYear);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getRuleType()
     */
    @Override
    public int getRuleType() {
        return RuleTypeEnum.DayMonthYearList;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#setRuleType(int)
     */
    @Override
    public void setRuleType(int ruleType) {
        super.setRuleType(RuleTypeEnum.DayMonthYearList);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#desc(android.content.res.Resources)
     */
    @Override
    public String desc(Resources res) {
        List<DayMonthYear> dayMonthYearList = getDayMonthYearList();
        if (JavaUtil.isEmpty(dayMonthYearList)) {
            return res.getString(R.string.ruleDescDayMonthYearListEmpty);
        } else {
            String sep = res.getString(R.string.ruleDescDayMonthYearListSep);

            StringBuilder buf = new StringBuilder();
            for (DayMonthYear dayMonthYear : dayMonthYearList) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, dayMonthYear.year);
                selectedDate.set(Calendar.MONTH, dayMonthYear.month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayMonthYear.day);

                if (buf.length() != 0) {
                    buf.append(sep);
                }
                buf.append(RACTimeDesc.descDateShort(selectedDate));
            }
            return buf.toString();
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#test(com.isjfk.android.rac.bean.Schedule)
     */
    @Override
    public boolean test(Calendar date) {
        List<DayMonthYear> dayMonthYearList = getDayMonthYearList();
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            if ((dayMonthYear.year == date.get(Calendar.YEAR))
                    && (dayMonthYear.month == date.get(Calendar.MONTH))
                    && (dayMonthYear.day == date.get(Calendar.DAY_OF_MONTH))) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.bean.DateRule#getExpireDate()
     */
    @Override
    public DayMonthYear getExpireDate() {
        List<DayMonthYear> dayMonthYearList = getDayMonthYearList();
        if (JavaUtil.isNotEmpty(dayMonthYearList)) {
            return dayMonthYearList.get(dayMonthYearList.size() - 1);
        }

        Calendar currDate = Calendar.getInstance();
        return new DayMonthYear(
                currDate.get(Calendar.DAY_OF_MONTH),
                currDate.get(Calendar.MONTH),
                currDate.get(Calendar.YEAR));
    }

    /**
     * 获取日期List。
     *
     * @return 日期List
     */
    public List<DayMonthYear> getDayMonthYearList() {
        List<DayMonthYear> dayMonthYearList = new ArrayList<DayMonthYear>();
        List<String> partList = RuleUtil.splitPart(getRule());
        for (String part : partList) {
            dayMonthYearList.add(RuleUtil.toDayMonthYear(part));
        }

        regular(dayMonthYearList);
        return dayMonthYearList;
    }

    /**
     * 设置日期List。
     *
     * @param dayMonthYearList 日期List
     */
    public void setDayMonthYearList(List<DayMonthYear> dayMonthYearList) {
        regular(dayMonthYearList);

        List<String> partList = new ArrayList<String>(dayMonthYearList.size());
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            partList.add(RuleUtil.toDayMonthYearStr(dayMonthYear));
        }
        setRule(RuleUtil.concatPart(partList));
    }

    /**
     * 格式化日期List。
     * 删除重复日期并按时间升序排序。
     *
     * @param dayMonthYearList 日期List
     */
    protected void regular(List<DayMonthYear> dayMonthYearList) {
        List<DayMonthYear> uniqueList = new ArrayList<DayMonthYear>(dayMonthYearList.size());
        for (DayMonthYear dayMonthYear : dayMonthYearList) {
            if (!uniqueList.contains(dayMonthYear)) {
                uniqueList.add(dayMonthYear);
            }
        }

        dayMonthYearList.clear();
        dayMonthYearList.addAll(uniqueList);

        Collections.sort(dayMonthYearList);
    }

}
