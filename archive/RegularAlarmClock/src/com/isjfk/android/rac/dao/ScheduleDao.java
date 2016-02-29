/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Schedule.Columns;
import com.isjfk.android.rac.bean.Schedule.Columns.ActivedEnum;
import com.isjfk.android.rac.bean.Schedule.Columns.EnabledEnum;
import com.isjfk.android.rac.common.BeanFactory;

/**
 * 闹铃日程数据Dao。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class ScheduleDao {

    /**
     * 查询所有闹铃日程。
     *
     * @param resolver ContentResolver
     * @return 所有闹铃日程
     */
    public List<Schedule> queryAll(ContentResolver resolver) {
        List<Schedule> scheduleList = new ArrayList<Schedule>();

        Cursor cursor = resolver.query(
                Schedule.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                null,
                null,
                Columns.SORT_ORDER);

        if (cursor != null) {
            try {
                Map<String, Integer> colIdxMap = getColumnIndexMap(cursor);
                while (cursor.moveToNext()) {
                    scheduleList.add(mapCursor2Schedule(cursor, colIdxMap));
                }
            } finally {
                cursor.close();
            }
        }

        return scheduleList;
    }

    /**
     * 查询闹铃日程。
     *
     * @param resolver ContentResolver
     * @param id 闹铃日程ID
     * @return 闹铃日程，如果不存在返回null
     */
    public Schedule query(ContentResolver resolver, int id) {
        Cursor cursor = resolver.query(
                Schedule.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) },
                Columns.SORT_ORDER);

        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return mapCursor2Schedule(cursor, getColumnIndexMap(cursor));
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * 增加一组闹铃日程。
     *
     * @param resolver ContentResolver
     * @param scheduleList 一组闹铃日程
     */
    public void add(ContentResolver resolver, List<Schedule> scheduleList) {
        for (Schedule schedule : scheduleList) {
            resolver.insert(Schedule.CONTENT_URI, mapSchedule2ContentValues(schedule));
        }
    }

    /**
     * 删除指定的闹铃日程。
     *
     * @param resolver ContentResolver
     */
    public void delete(ContentResolver resolver, int id) {
        resolver.delete(
                Schedule.CONTENT_URI,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 修改闹铃日程。
     *
     * @param resolver ContentResolver
     * @param schedule 闹铃日程
     * @return 修改的闹铃日程数量
     */
    public int update(ContentResolver resolver, Schedule schedule) {
        return resolver.update(
                Schedule.CONTENT_URI,
                mapSchedule2ContentValues(schedule),
                Columns.WHERE_ID,
                new String[] { String.valueOf(schedule.getId()) });
    }

    /**
     * 删除所有闹铃日程。
     *
     * @param resolver ContentResolver
     */
    public void deleteAll(ContentResolver resolver) {
        resolver.delete(Schedule.CONTENT_URI, "1", null);
    }

    /**
     * 修改闹铃日程是否启用状态。
     *
     * @param resolver ContentResolver
     * @param id 闹铃日程ID
     * @param enabled 闹铃是否启用
     */
    public int updateEnabled(ContentResolver resolver, int id, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(Columns.ENABLED, enabled ? EnabledEnum.TRUE : EnabledEnum.FALSE);

        return resolver.update(
                Schedule.CONTENT_URI,
                values,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 修改闹铃日程是否激活状态。
     *
     * @param resolver ContentResolver
     * @param id 闹铃日程ID
     * @param actived 闹铃是否激活
     */
    public int updateActived(ContentResolver resolver, int id, boolean actived) {
        ContentValues values = new ContentValues();
        values.put(Columns.ACTIVED, actived ? ActivedEnum.TRUE : ActivedEnum.FALSE);

        return resolver.update(
                Schedule.CONTENT_URI,
                values,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    private Map<String, Integer> getColumnIndexMap(Cursor cursor) {
        Map<String, Integer> colIdxMap = new HashMap<String, Integer>();
        colIdxMap.put(Columns._ID, cursor.getColumnIndex(Columns._ID));
        colIdxMap.put(Columns.NAME, cursor.getColumnIndex(Columns.NAME));
        colIdxMap.put(Columns.DATETIME, cursor.getColumnIndex(Columns.DATETIME));
        colIdxMap.put(Columns.ALARMRULEID, cursor.getColumnIndex(Columns.ALARMRULEID));
        colIdxMap.put(Columns.ENABLED, cursor.getColumnIndex(Columns.ENABLED));
        colIdxMap.put(Columns.ACTIVED, cursor.getColumnIndex(Columns.ACTIVED));
        colIdxMap.put(Columns.TIMEOUTTIMES, cursor.getColumnIndex(Columns.TIMEOUTTIMES));
        return colIdxMap;
    }

    private Schedule mapCursor2Schedule(Cursor cursor, Map<String, Integer> colIdxMap) {
        Schedule schedule = BeanFactory.newSchedule();
        schedule.setId(cursor.getInt(colIdxMap.get(Columns._ID)));
        schedule.setName(cursor.getString(colIdxMap.get(Columns.NAME)));
        schedule.setDateTime(cursor.getString(colIdxMap.get(Columns.DATETIME)));
        schedule.setAlarmRuleId(cursor.getInt(colIdxMap.get(Columns.ALARMRULEID)));
        schedule.setEnabled(EnabledEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.ENABLED))));
        schedule.setActived(ActivedEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.ACTIVED))));
        schedule.setTimeoutTimes(cursor.getInt(colIdxMap.get(Columns.TIMEOUTTIMES)));
        return schedule;
    }

    private ContentValues mapSchedule2ContentValues(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME, schedule.getName());
        values.put(Columns.DATETIME, schedule.getDateTime());
        values.put(Columns.ALARMRULEID, schedule.getAlarmRuleId());
        values.put(Columns.ENABLED, schedule.isEnabled() ? EnabledEnum.TRUE : EnabledEnum.FALSE);
        values.put(Columns.ACTIVED, schedule.isActived() ? ActivedEnum.TRUE : ActivedEnum.FALSE);
        values.put(Columns.TIMEOUTTIMES, schedule.getTimeoutTimes());
        return values;
    }

}
