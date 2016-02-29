/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.EModeEnum;
import com.isjfk.android.rac.bean.other.DayMonth;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.bean.rule.DayMonthYearRangeRule;
import com.isjfk.android.rac.common.RACConstant;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.util.JavaUtil;

/**
 * 日期规则工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-3
 */
public class RuleUtil {

    /**
     * 将"|"分隔的规则字符串分解为字符串List。
     *
     * @param ruleStr "|"分隔的规则字符串
     * @return 分解后的字符串List。如果ruleStr为空返回空List，如果ruleStr中有字符但没有"|"返回长度为1的List
     */
    public static List<String> splitPart(String ruleStr) {
        List<String> partList = new ArrayList<String>();
        if (JavaUtil.isEmpty(ruleStr)) {
            return partList;
        }
        String[] partArr = ruleStr.split(RACConstant.RULE_SEP_PART_REGEX);
        for (String part : partArr) {
            partList.add(JavaUtil.trimToEmpty(part));
        }
        return partList;
    }

    /**
     * 将字符串List组合为"|"分隔的规则字符串。
     *
     * @param ruleStr 字符串List
     * @return "|"分隔的规则字符串
     */
    public static String concatPart(List<String> rulePartList) {
        StringBuilder buf = new StringBuilder();
        for (String rulePart : rulePartList) {
            if (buf.length() != 0) {
                buf.append(RACConstant.RULE_SEP_PART);
            }
            buf.append(JavaUtil.trimToEmpty(rulePart));
        }
        return buf.toString();
    }

    /**
     * 将逗号分隔的整数字符串转换成整数List。
     *
     * @param commaSepIntStr 逗号分隔的整数字符串
     * @return 整数List
     */
    public static List<Integer> toIntList(String commaSepIntStr) {
        return JavaUtil.toIntList(commaSepIntStr, RACConstant.RULE_SEP_COMMA);
    }

    /**
     * 将整数List转换为逗号分隔的整数字符串。
     *
     * @param intList 整数List
     * @return 逗号分隔的整数字符串
     */
    public static String toCommaSepIntStr(List<Integer> intList) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < intList.size(); i++) {
            if (i != 0) {
                buf.append(RACConstant.RULE_SEP_COMMA);
            }
            buf.append(intList.get(i));
        }
        return buf.toString();
    }

    /**
     * 将逗号分隔的星期字符串转换成星期List。
     *
     * @param commaSepDayOfWeekStr 逗号分隔的星期字符串
     * @return 星期List
     */
    public static List<Integer> toDayOfWeekList(String commaSepDayOfWeekStr) {
        List<Integer> dayOfWeekList = toIntList(commaSepDayOfWeekStr);
        Collections.sort(dayOfWeekList);
        if (dayOfWeekList.contains(Calendar.SUNDAY) && !RACContext.isFirstDaySunday()) {
            // call the remove by object method, not by index
            dayOfWeekList.remove(Integer.valueOf(Calendar.SUNDAY));
            dayOfWeekList.add(Calendar.SUNDAY);
        }
        return dayOfWeekList;
    }

    /**
     * 将星期List转换为逗号分隔的字符串。
     *
     * @param dayOfWeekList 星期List
     * @return 逗号分隔的字符串
     */
    public static String toDayOfWeekStr(List<Integer> dayOfWeekList) {
        Collections.sort(dayOfWeekList);
        return toCommaSepIntStr(dayOfWeekList);
    }

    /**
     * 将逗号分隔的日期字符串转换成日期List。
     *
     * @param commaSepDayOfMonthStr 逗号分隔的日期字符串
     * @return 日期List
     */
    public static List<Integer> toDayOfMonthList(String commaSepDayOfMonthStr) {
        List<Integer> dayOfMonthList = toIntList(commaSepDayOfMonthStr);
        Collections.sort(dayOfMonthList);
        return dayOfMonthList;
    }

    /**
     * 将日期List转换为逗号分隔的字符串。
     *
     * @param dayOfMonthList 日期List
     * @return 逗号分隔的字符串
     */
    public static String toDayOfMonthStr(List<Integer> dayOfMonthList) {
        Collections.sort(dayOfMonthList);
        return toCommaSepIntStr(dayOfMonthList);
    }

    /**
     * 将"."分隔的年月字符串转换成DayMonth。
     *
     * @param pointSepDayMonthStr "."分隔的年月字符串
     * @return DayMonth
     */
    public static DayMonth toDayMonth(String pointSepDayMonthStr) {
        List<Integer> list = JavaUtil.toIntList(pointSepDayMonthStr, RACConstant.RULE_SEP_POINT_REGEX);
        return new DayMonth(list.get(0), list.get(1));
    }

    /**
     * 将DayMonth转换成"."分隔的年月字符串。
     *
     * @param dayMonth DayMonth
     * @return "."分隔的年月字符串
     */
    public static String toDayMonthStr(DayMonth dayMonth) {
        return dayMonth.day + RACConstant.RULE_SEP_POINT + dayMonth.month;
    }

    /**
     * 将"."分隔的年月日字符串转换成DayMonthYear。
     *
     * @param pointSepDayMonthYearStr "."分隔的年月日字符串
     * @return DayMonthYear
     */
    public static DayMonthYear toDayMonthYear(String pointSepDayMonthYearStr) {
        List<Integer> list = JavaUtil.toIntList(pointSepDayMonthYearStr, RACConstant.RULE_SEP_POINT_REGEX);
        return new DayMonthYear(list.get(0), list.get(1), list.get(2));
    }

    /**
     * 将DayMonthYear转换成"."分隔的年月日字符串。
     *
     * @param dayMonthYear DayMonthYear
     * @return "."分隔的年月日字符串
     */
    public static String toDayMonthYearStr(DayMonthYear dayMonthYear) {
        return dayMonthYear.day + RACConstant.RULE_SEP_POINT
                + dayMonthYear.month + RACConstant.RULE_SEP_POINT
                + dayMonthYear.year;
    }

    /**
     * 获取date表示的星期是当月的第几个。
     *
     * @param date 日期
     * @return date表示的星期是当月的第几个
     */
    public static int getOrderInMonth(Calendar date) {
        int maxDayInWeek = date.getActualMaximum(Calendar.DAY_OF_WEEK);

        return (date.get(Calendar.DAY_OF_MONTH) - 1) / maxDayInWeek + 1;
    }

    /**
     * 获取date表示的星期是当月的倒数第几个。
     *
     * @param date 日期
     * @return date表示的星期是当月的倒数第几个
     */
    public static int getRevOrderInMonth(Calendar date) {
        int maxDayInMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        int maxDayInWeek = date.getActualMaximum(Calendar.DAY_OF_WEEK);

        return (maxDayInMonth - date.get(Calendar.DAY_OF_MONTH)) / maxDayInWeek + 1;
    }



    /**
     * 判断日期规则是否为一次性。
     *
     * @param dateRuleList 日期规则
     * @return 如果日期规则为一次性返回true，否则返回false
     */
    public static boolean isOneTime(List<DateRule> dateRuleList) {
        return JavaUtil.isEmpty(dateRuleList);
    }

    /**
     * 判断日期规则是否会过期。
     *
     * @param dateRuleList 日期规则
     * @return 日期规则会过期返回true，否则返回false
     */
    public static boolean isExpireable(List<DateRule> dateRuleList) {
        return getExpireDate(dateRuleList) != null;
    }

    /**
     * 获取日期规则的失效日期。
     *
     * @param dateRuleList 日期规则
     * @return 日期规则的失效日期，如果永远不失效或无法准确判断则返回null
     */
    public static DayMonthYear getExpireDate(List<DateRule> dateRuleList) {
        DayMonthYear expireDate = null;
        if (isOneTime(dateRuleList)) {
            // 一次性闹铃响铃后，获取的失效日期与响铃日期一致
            Calendar cal = Calendar.getInstance();
            expireDate = new DayMonthYear(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR));
        } else {
            expireDate = dateRuleList.get(0).getExpireDate();

            for (int i = 1; i < dateRuleList.size(); i++) {
                DateRule dateRule = dateRuleList.get(i);

                int eMode = dateRule.getEMode();
                if (eMode == EModeEnum.ADD) {
                    DayMonthYear nextExpireDate = dateRule.getExpireDate();
                    if ((nextExpireDate == null) || (expireDate == null)) {
                        expireDate = null;
                    } else if (nextExpireDate.compareTo(expireDate) > 0) {
                        expireDate = nextExpireDate;
                    }
                } else if (eMode == EModeEnum.SUBTRACT) {
                    DayMonthYear nextExpireDate = dateRule.getExpireDate();
                    if (!((expireDate != null) && (nextExpireDate != null) && (nextExpireDate.compareTo(expireDate) < 0))) {
                        // 无法判断
                        expireDate = null;
                    }
                } else if (eMode == EModeEnum.WITHIN) {
                    if ((dateRule instanceof DayMonthYearRangeRule) && (expireDate != null)) {
                        DayMonthYearRangeRule dmyrRule = (DayMonthYearRangeRule) dateRule;

                        DayMonthYear startDate = dmyrRule.getStartDayMonthYear();
                        DayMonthYear endDate = dmyrRule.getEndDayMonthYear();
                        if (!((expireDate.compareTo(startDate) >= 0) && (expireDate.compareTo(endDate) <= 0))) {
                            // 无法判断
                            expireDate = null;
                        }
                    } else {
                        // 无法判断
                        expireDate = null;
                    }
                }
            }
        }

        return expireDate;
    }

}
