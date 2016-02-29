/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.EModeEnum;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.RACUtil;

/**
 * 规则闹钟算法。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-3
 */
public class RegularAlarmAlgorithm {

    /**
     * 生成闹铃日程列表。
     *
     * @param alarmRuleList 闹铃规则列表
     * @param workdayList 工作日列表
     * @param orgScheduleList 原有闹铃日程列表
     * @param startDate 闹铃日程开始日期
     * @param endDate 闹铃日程结束日期
     * @return 闹铃日程列表
     */
    public static List<Schedule> genScheduleList(
            List<AlarmRule> alarmRuleList,
            List<Workday> workdayList,
            List<Schedule> orgScheduleList,
            Calendar startDate,
            Calendar endDate) {
        Workday workday = findActiveWorkday(workdayList);

        List<Schedule> scheduleList = new ArrayList<Schedule>();
        for (AlarmRule alarmRule : alarmRuleList) {
            scheduleList = mergeScheduleList(
                    scheduleList,
                    createScheduleList(alarmRule, workday, orgScheduleList, startDate, endDate));
        }
        return scheduleList;
    }

    /**
     * 设置闹铃日程的actived状态。
     *
     * @param scheduleList 需要设置actived状态的闹铃日程列表
     * @param alarmRuleList 所有闹铃规则
     * @param workdayList 所有工作日
     */
    public static void setScheduleListActived(
            List<Schedule> scheduleList,
            List<AlarmRule> alarmRuleList,
            List<Workday> workdayList) {
        Workday workday = findActiveWorkday(workdayList);
        for (Schedule schedule : scheduleList) {
            AlarmRule alarmRule = findAlarmRule(alarmRuleList, schedule.getAlarmRuleId());
            setScheduleActived(schedule, alarmRule, workday);
        }
    }

    /**
     * 创建闹铃规则对应的日程对象。
     *
     * @param alarmRule 闹铃规则
     * @param workday 工作日
     * @param orgScheduleList 原有闹铃日程列表
     * @param length 创建的闹铃日程列表长度
     * @return 闹铃日程列表
     */
    private static List<Schedule> createScheduleList(
            AlarmRule alarmRule,
            Workday workday,
            List<Schedule> orgScheduleList,
            Calendar startDate,
            Calendar endDate) {
        Calendar currTime = Calendar.getInstance();

        List<Schedule> scheduleList = new ArrayList<Schedule>();
        if (alarmRule.isEnabled()) {
            if (alarmRule.isOneTime()) {
                Calendar time = genAlarmTime(currTime, alarmRule.getHour(), alarmRule.getMinute());
                if (time.compareTo(currTime) <= 0) {
                    time.add(Calendar.DAY_OF_MONTH, 1);
                }
                if ((time.compareTo(startDate) >= 0) && (time.compareTo(endDate) <= 0)) {
                    scheduleList.add(newSchedule(alarmRule, time, workday, orgScheduleList));
                }
            } else {
                for (Calendar timeTmp = (Calendar) startDate.clone();
                        timeTmp.compareTo(endDate) <= 0;
                        timeTmp.add(Calendar.DAY_OF_MONTH, 1)) {
                    Calendar time = genAlarmTime(timeTmp, alarmRule.getHour(), alarmRule.getMinute());
                    if ((time.compareTo(currTime) > 0) && isComplyDateRule(time, alarmRule.getDateRuleList())) {
                        scheduleList.add(newSchedule(alarmRule, time, workday, orgScheduleList));
                    }
                }
            }
        }

        return scheduleList;
    }

    /**
     * 生成响铃时间。
     *
     * @param timeTemplate 时间模板
     * @param hour 响铃小时
     * @param minute 响铃分钟
     * @return 响铃时间
     */
    private static Calendar genAlarmTime(Calendar timeTemplate, int hour, int minute) {
        Calendar time = (Calendar) timeTemplate.clone();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        return time;
    }

    /**
     * 创建闹铃日程。
     *
     * @param alarmRule 闹铃日程对应的闹铃规则
     * @param time 响铃时间
     * @param workday 工作日
     * @param orgScheduleList 原有闹铃日程列表
     * @return 闹铃日程
     */
    private static Schedule newSchedule(
            AlarmRule alarmRule,
            Calendar time,
            Workday workday,
            List<Schedule> orgScheduleList) {
        Schedule schedule = BeanFactory.newSchedule();
        schedule.setAlarmRuleId(alarmRule.getId());
        schedule.setTime(time);
        schedule.setName(alarmRule.getName());

        setScheduleActived(schedule, alarmRule, workday);

        Schedule orgSchedule = findOrgSchedule(orgScheduleList, schedule);
        if (orgSchedule != null) {
            schedule.setEnabled(orgSchedule.isEnabled());
        } else {
            schedule.setEnabled(alarmRule.isEnabled());
        }

        return schedule;
    }

    /**
     * 设置闹铃日程的actived状态。
     *
     * @param schedule 需要设置actived状态的闹铃日程
     * @param alarmRule 闹铃规则
     * @param workday 工作日
     */
    private static void setScheduleActived(Schedule schedule, AlarmRule alarmRule, Workday workday) {
        if (alarmRule == null) {
            schedule.setActived(false);
        } else {
            boolean complyWorkday = true;
            if (alarmRule.isComplyWorkday() && (workday != null)) {
                complyWorkday = isComplyDateRule(schedule.getTime(), workday.getDateRuleList());
            }
            schedule.setActived(complyWorkday && alarmRule.isActived());
        }
    }

    /**
     * 按响铃时间顺序合并两个闹铃日程列表。
     *
     * @param scheduleList1 闹铃日程列表1
     * @param scheduleList2 闹铃日程列表2
     * @return 合并后的闹铃日程列表
     */
    private static List<Schedule> mergeScheduleList(List<Schedule> scheduleList1, List<Schedule> scheduleList2) {
        List<Schedule> scheduleList = new ArrayList<Schedule>(scheduleList1.size() + scheduleList2.size());

        int index1 = 0;
        int index2 = 0;
        while ((index1 < scheduleList1.size()) && (index2 < scheduleList2.size())) {
            Schedule schedule1 = scheduleList1.get(index1);
            Schedule schedule2 = scheduleList2.get(index2);
            if (schedule1.getTime().compareTo(schedule2.getTime()) <= 0) {
                scheduleList.add(schedule1);
                index1++;
            } else {
                scheduleList.add(schedule2);
                index2++;
            }
        }

        if (index1 < scheduleList1.size()) {
            scheduleList.addAll(scheduleList1.subList(index1, scheduleList1.size()));
        }
        if (index2 < scheduleList2.size()) {
            scheduleList.addAll(scheduleList2.subList(index2, scheduleList2.size()));
        }

        return scheduleList;
    }

    /**
     * 查找当前生效的工作日。
     *
     * @param workdayList 工作日列表
     * @return 第一个生效的工作日，如果找不到返回null
     */
    private static Workday findActiveWorkday(List<Workday> workdayList) {
        for (Workday workday : workdayList) {
            if (workday.isActived()) {
                return workday;
            }
        }
        return null;
    }

    /**
     * 查找闹铃日程在原有闹铃日程列表中的对应记录。
     *
     * @param orgScheduleList 原有闹铃日程列表
     * @param schedule 新闹铃日程
     * @return 新闹铃日程对应的原闹铃日程，如果没有返回null
     */
    private static Schedule findOrgSchedule(List<Schedule> orgScheduleList, Schedule schedule) {
        for (Schedule orgSchedule : orgScheduleList) {
            if (orgSchedule.getAlarmRuleId() != schedule.getAlarmRuleId()) {
                continue;
            }
            if (RACUtil.isSnoozed(orgSchedule)) {
                continue;
            }

            Calendar orgTime = orgSchedule.getTime();
            Calendar newTime = schedule.getTime();
            if ((orgTime.get(Calendar.YEAR) == newTime.get(Calendar.YEAR))
                    && (orgTime.get(Calendar.MONTH) == newTime.get(Calendar.MONTH))
                    && (orgTime.get(Calendar.DAY_OF_MONTH) == newTime.get(Calendar.DAY_OF_MONTH))) {
                return orgSchedule;
            }
        }
        return null;
    }

    /**
     * 根据ID查找闹铃规则。
     *
     * @param alarmRuleList 所有闹铃规则
     * @param alarmRuleId 需要查找的闹铃规则ID
     * @return 闹铃规则，如果找不到返回null
     */
    private static AlarmRule findAlarmRule(List<AlarmRule> alarmRuleList, int alarmRuleId) {
        for (AlarmRule alarmRule : alarmRuleList) {
            if (alarmRule.getId() == alarmRuleId) {
                return alarmRule;
            }
        }
        return null;
    }

    /**
     * 判断时间是否符合日期规则
     *
     * @param time 时间
     * @param dateRuleList 日期规则列表
     */
    private static boolean isComplyDateRule(Calendar time, List<DateRule> dateRuleList) {
        boolean complyDateRule = false;
        for (DateRule dateRule : dateRuleList) {
            if (EModeEnum.WITHIN.equals(dateRule.getEMode())) {
                complyDateRule &= dateRule.test(time);
            } else if (EModeEnum.ADD.equals(dateRule.getEMode())) {
                complyDateRule |= dateRule.test(time);
            } else if (EModeEnum.SUBTRACT.equals(dateRule.getEMode())) {
                complyDateRule &= !dateRule.test(time);
            }
        }
        return complyDateRule;
    }

}
