/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.util.Calendar;

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.AlarmRule.Columns.RingEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.VibrateEnum;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.bean.LocArea;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.bean.rule.AlarmXOffYDaysRule;
import com.isjfk.android.rac.bean.rule.DayMonthRangeRule;
import com.isjfk.android.rac.bean.rule.DayMonthYearRangeRule;
import com.isjfk.android.rac.bean.rule.DayOfMonthRule;
import com.isjfk.android.rac.bean.rule.DayOfWeekInMonthRule;
import com.isjfk.android.rac.bean.rule.DayOfWeekRule;
import com.isjfk.android.rac.bean.rule.EveryDayRule;
import com.isjfk.android.rac.bean.rule.RevDayOfWeekInMonthRule;
import com.isjfk.android.rac.bean.rule.DayMonthYearListRule;

/**
 * Bean工厂。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-4
 */
public class BeanFactory {

    /**
     * 构造闹铃日程对象。
     *
     * @return 闹铃日程对象
     */
    public static Schedule newSchedule() {
        Schedule schedule = new Schedule();
        schedule.setTimeoutTimes(0);
        return schedule;
    }

    /**
     * 构造闹铃规则对象。
     *
     * @return 闹铃规则对象
     */
    public static AlarmRule newAlarmRule() {
        Calendar currTime = Calendar.getInstance();
        RACUtil.adjustMinuteSegment(currTime);

        AlarmRule alarmRule = new AlarmRule();
        alarmRule.setTime(currTime.get(Calendar.HOUR_OF_DAY), currTime.get(Calendar.MINUTE));
        alarmRule.setDelAfterExpired(true);
        alarmRule.setComplyWorkday(false);
        alarmRule.setLocationId(LocArea.DEFAULT_ALL_LOC_ID);
        alarmRule.setRing(RingEnum.DEFAULT);
        alarmRule.setRingtone(newRingtoneConfig());
        alarmRule.setVibrate(VibrateEnum.DEFAULT);
        alarmRule.setEnabled(true);
        alarmRule.setActived(true);

        return alarmRule;
    }

    /**
     * 构造工作日对象。
     *
     * @return 工作日对象
     */
    public static Workday newWorkday() {
        Workday workday = new Workday();
        workday.setLocationId(LocArea.DEFAULT_ALL_LOC_ID);
        workday.setActived(true);
        return workday;
    }

    /**
     * 根据日期规则类型构造日期规则对象。
     *
     * @param ruleType 日期规则类型
     * @return 日期规则对象，如果类型非法则返回null
     */
    public static DateRule newDateRule(Integer ruleType) {
        DateRule rule = null;
        if (RuleTypeEnum.EveryDay.equals(ruleType)) {
            rule = new EveryDayRule();
        } else if (RuleTypeEnum.DayOfWeek.equals(ruleType)) {
            rule = new DayOfWeekRule();
        } else if (RuleTypeEnum.DayOfMonth.equals(ruleType)) {
            rule = new DayOfMonthRule();
        } else if (RuleTypeEnum.DayOfWeekInMonth.equals(ruleType)) {
            rule = new DayOfWeekInMonthRule();
        } else if (RuleTypeEnum.RevDayOfWeekInMonth.equals(ruleType)) {
            rule = new RevDayOfWeekInMonthRule();
        } else if (RuleTypeEnum.DayMonthRange.equals(ruleType)) {
            rule = new DayMonthRangeRule();
        } else if (RuleTypeEnum.DayMonthYearRange.equals(ruleType)) {
            rule = new DayMonthYearRangeRule();
        } else if (RuleTypeEnum.AlarmXOffYDays.equals(ruleType)) {
            rule = new AlarmXOffYDaysRule();
        } else if (RuleTypeEnum.DayMonthYearList.equals(ruleType)) {
            rule = new DayMonthYearListRule();
        }

        return rule;
    }

    /**
     * 构造响铃铃声配置。
     *
     * @return 响铃铃声配置
     */
    public static RingtoneConfig newRingtoneConfig() {
        RingtoneConfig ringtoneConfig = new RingtoneConfig();
        ringtoneConfig.setType(RingtoneConfig.TYPE_PREFERENCE);
        return ringtoneConfig;
    }

}
