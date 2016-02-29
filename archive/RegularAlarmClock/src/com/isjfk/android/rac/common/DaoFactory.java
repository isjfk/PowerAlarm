/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

import com.isjfk.android.rac.dao.AlarmRuleDao;
import com.isjfk.android.rac.dao.ConfigDao;
import com.isjfk.android.rac.dao.DateRuleDao;
import com.isjfk.android.rac.dao.ScheduleDao;
import com.isjfk.android.rac.dao.WorkdayDao;

/**
 * Dao工厂。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class DaoFactory {

    private static DaoFactory instance = new DaoFactory();

    private ConfigDao configDao = new ConfigDao();
    private ScheduleDao scheduleDao = new ScheduleDao();
    private AlarmRuleDao alarmRuleDao = new AlarmRuleDao();
    private WorkdayDao workdayDao = new WorkdayDao();
    private DateRuleDao dateRuleDao = new DateRuleDao();

    /**
     * 获取Dao工厂实例。
     *
     * @return Dao工厂实例
     */
    public static DaoFactory getInstance() {
        return instance;
    }

    /**
     * 获取ConfigDao实例。
     *
     * @return ConfigDao实例
     */
    public ConfigDao getConfigDao() {
        return configDao;
    }

    /**
     * 获取ScheduleDao实例。
     *
     * @return ScheduleDao实例
     */
    public ScheduleDao getScheduleDao() {
        return scheduleDao;
    }

    /**
     * 获取AlarmRuleDao实例。
     *
     * @return AlarmRuleDao实例
     */
    public AlarmRuleDao getAlarmRuleDao() {
        return alarmRuleDao;
    }

    /**
     * 获取WorkdayDao实例。
     *
     * @return WorkdayDao实例
     */
    public WorkdayDao getWorkdayDao() {
        return workdayDao;
    }

    /**
     * 获取DateRuleDao实例。
     *
     * @return DateRuleDao实例
     */
    public DateRuleDao getDateRuleDao() {
        return dateRuleDao;
    }

}
