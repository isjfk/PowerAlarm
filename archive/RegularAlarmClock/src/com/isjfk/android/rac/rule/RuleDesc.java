/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.rule;

import java.util.Calendar;
import java.util.List;

import android.content.res.Resources;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.util.JavaUtil;

/**
 * 日期规则描述生成工具类。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-3
 */
public class RuleDesc {

    private static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * 用单行描述一组日期规则。
     *
     * @param res 应用资源
     * @param ruleList 一组日期规则
     * @return 日期规则的单行描述
     */
    public static String descSingleLine(Resources res, List<DateRule> ruleList) {
        String[] eModes = res.getStringArray(R.array.dateRuleEMode);
        String desc = "";
        for (int i = 0; i < ruleList.size(); i++) {
            DateRule rule = ruleList.get(i);
            if (JavaUtil.isEmpty(desc)) {
                desc = rule.desc(res);
            } else {
                desc = res.getString(R.string.ruleDescSingleLine, desc, eModes[rule.getEMode()], rule.desc(res));
            }
        }

        return desc;
    }

    /**
     * 用多行描述一组日期规则。
     *
     * @param res 应用资源
     * @param ruleList 一组日期规则
     * @return 日期规则的多行描述
     */
    public static String descMultiLine(Resources res, List<DateRule> ruleList) {
        String[] eModes = res.getStringArray(R.array.dateRuleEMode);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < ruleList.size(); i++) {
            DateRule rule = ruleList.get(i);
            if (buf.length() != 0) {
                buf.append(LINE_SEP).append(eModes[rule.getEMode()]).append(LINE_SEP);
            }
            buf.append(rule.desc(res));
        }

        return buf.toString();
    }

    /**
     * 将数字表示的星期列表转换为文本形式。
     *
     * @param res 应用资源
     * @param dayOfWeekList 数字表示的星期列表
     * @return 文本表示的星期列表
     */
    public static String dayOfWeekList2Str(Resources res, List<Integer> dayOfWeekList) {
        StringBuilder sb = new StringBuilder();
        if (JavaUtil.isEmpty(dayOfWeekList)) {
            sb.append(res.getString(R.string.ruleDescNoDay));
        } else {
            String sep = res.getString(R.string.ruleDescDayOfWeekSep);
            for (Integer dayOfWeek : dayOfWeekList) {
                String dayOfWeekStr = getDayOfWeekShort(res, dayOfWeek);
                if (JavaUtil.isNotEmpty(dayOfWeekStr)) {
                    if (sb.length() != 0) {
                        sb.append(sep);
                    }
                    sb.append(dayOfWeekStr);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取数字表示的星期对应的文字形式。
     *
     * @param res 应用资源
     * @param dayOfWeek 数字表示的星期
     * @return 星期的文本形式
     */
    public static String getDayOfWeekShort(Resources res, Integer dayOfWeek) {
        if ((dayOfWeek >= Calendar.SUNDAY) && (dayOfWeek <= Calendar.SATURDAY)) {
            return res.getStringArray(R.array.dayOfWeekNameShort)[dayOfWeek - 1];
        } else {
            Log.e("unknown dayOfWeek: " + dayOfWeek);
        }
        return res.getString(R.string.ruleDescUnknown);
    }

    /**
     * 将数字表示的日期列表转换为文本形式。
     *
     * @param res 应用资源
     * @param dayOfMonthList 数字表示的日期列表
     * @return 文本表示的日期列表
     */
    public static String dayOfMonthList2Str(Resources res, List<Integer> dayOfMonthList) {
        StringBuilder sb = new StringBuilder();
        if (JavaUtil.isEmpty(dayOfMonthList)) {
            sb.append(res.getString(R.string.ruleDescNoDay));
        } else {
            String sep = res.getString(R.string.ruleDescDayOfMonthSep);
            for (Integer dayOfMonth : dayOfMonthList) {
                if (sb.length() != 0) {
                    sb.append(sep);
                }
                sb.append(dayOfMonth);
            }
        }
        return sb.toString();
    }

    /**
     * 获取顺序的文本形式。
     *
     * @param res 应用资源
     * @param order 顺序
     * @return 顺序的文本形式
     */
    public static String getOrder(Resources res, Integer order) {
        String[] strArr = res.getStringArray(R.array.order);
        if ((order >= 1) && (order <= strArr.length)) {
            return strArr[order - 1];
        } else {
            Log.e("unknown order: " + order);
            return res.getString(R.string.ruleDescUnknown);
        }
    }

    /**
     * 获取月份的文本描述。
     *
     * @param res 应用资源
     * @param month 月份，1月用0表示
     * @return 月份的文本描述
     */
    public static String getMonth(Resources res, Integer month) {
        String[] strArr = res.getStringArray(R.array.monthName);
        if ((month >= 0) && (month < strArr.length)) {
            return strArr[month];
        } else {
            Log.e("unknown month: " + month);
            return res.getString(R.string.ruleDescUnknown);
        }
    }

    /**
     * 获取月份的文本描述简写。
     *
     * @param res 应用资源
     * @param month 月份，1月用0表示
     * @return 月份的文本描述简写
     */
    public static String getMonthShort(Resources res, Integer month) {
        String[] strArr = res.getStringArray(R.array.monthNameShort);
        if ((month >= 0) && (month < strArr.length)) {
            return strArr[month];
        } else {
            Log.e("unknown month: " + month);
            return res.getString(R.string.ruleDescUnknown);
        }
    }

}
