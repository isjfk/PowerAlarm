/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.bean.Workday.Columns;
import com.isjfk.android.rac.bean.Workday.Columns.ActivedEnum;
import com.isjfk.android.rac.common.BeanFactory;

/**
 * 工作日Dao。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class WorkdayDao {

    /**
     * 查询所有工作日。
     *
     * @param resolver ContentResolver
     * @return 工作日列表，如果不存在返回空List
     */
    public List<Workday> queryAll(ContentResolver resolver) {
        List<Workday> workdayList = new ArrayList<Workday>();

        Cursor cursor = resolver.query(
                Workday.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                null,
                null,
                Columns.SORT_ORDER);

        if (cursor != null) {
            try {
                Map<String, Integer> colIdxMap = getColumnIndexMap(cursor);
                while (cursor.moveToNext()) {
                    workdayList.add(mapCursor2Workday(cursor, colIdxMap));
                }
            } finally {
                cursor.close();
            }
        }

        return workdayList;
    }

    /**
     * 查询工作日。
     *
     * @param resolver ContentResolver
     * @param id 工作日ID
     * @return 工作日，如果不存在返回null
     */
    public Workday query(ContentResolver resolver, int id) {
        Cursor cursor = resolver.query(
                Workday.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) },
                Columns.SORT_ORDER);

        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return mapCursor2Workday(cursor, getColumnIndexMap(cursor));
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * 增加工作日。
     *
     * @param resolver ContentResolver
     * @param workday 工作日
     * @return 增加的工作日ID
     */
    public int add(ContentResolver resolver, Workday workday) {
        Uri uri = resolver.insert(Workday.CONTENT_URI, mapWorkday2ContentValues(workday));
        return (int) ContentUris.parseId(uri);
    }

    /**
     * 删除工作日。
     *
     * @param resolver ContentResolver
     * @param id 工作日ID
     * @return 删除的工作日数量
     */
    public int delete(ContentResolver resolver, int id) {
        return resolver.delete(
                Workday.CONTENT_URI,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 修改工作日。
     *
     * @param resolver ContentResolver
     * @param workday 工作日
     * @return 修改的工作日数量
     */
    public int update(ContentResolver resolver, Workday workday) {
        return resolver.update(
                Workday.CONTENT_URI,
                mapWorkday2ContentValues(workday),
                Columns.WHERE_ID,
                new String[] { String.valueOf(workday.getId()) });
    }

    /**
     * 修改工作日是否活动状态。
     *
     * @param resolver ContentResolver
     * @param id 工作日ID
     * @param actived 工作日是否活动
     * @return 修改的工作日数目
     */
    public int updateActived(ContentResolver resolver, int id, boolean actived) {
        ContentValues values = new ContentValues();
        values.put(Columns.ACTIVED, actived ? ActivedEnum.TRUE : ActivedEnum.FALSE);

        return resolver.update(
                Workday.CONTENT_URI,
                values,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 检查位置是否可用。
     * 一个位置只能创建一个工作日。
     *
     * @param resolver ContentResolver
     * @param locationId 位置ID
     * @return true表示位置可用，false表示位置不可用
     */
    public boolean locationUsable(ContentResolver resolver, int locationId) {
        return locationUsable(resolver, -1, locationId);
    }

    /**
     * 检查位置是否可用。
     * 用于修改工作日时，检查位置是否被别的工作日使用。
     *
     * @param resolver ContentResolver
     * @param workdayId 工作日ID
     * @param locationId 位置ID
     * @return true表示位置可用，false表示位置不可用
     */
    public boolean locationUsable(ContentResolver resolver, int workdayId, int locationId) {
        Cursor cursor = resolver.query(
                Workday.CONTENT_URI,
                Columns.QUERY_SIZE,
                Columns.WHERE_LOCID,
                new String[] { String.valueOf(workdayId), String.valueOf(locationId) },
                Columns.SORT_ORDER);
        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return cursor.getInt(0) == 0;
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    private Map<String, Integer> getColumnIndexMap(Cursor cursor) {
        Map<String, Integer> colIdxMap = new HashMap<String, Integer>();
        colIdxMap.put(Columns._ID, cursor.getColumnIndex(Columns._ID));
        colIdxMap.put(Columns.NAME, cursor.getColumnIndex(Columns.NAME));
        colIdxMap.put(Columns.DATERULEID, cursor.getColumnIndex(Columns.DATERULEID));
        colIdxMap.put(Columns.LOCATIONID, cursor.getColumnIndex(Columns.LOCATIONID));
        colIdxMap.put(Columns.ACTIVED, cursor.getColumnIndex(Columns.ACTIVED));
        return colIdxMap;
    }

    private Workday mapCursor2Workday(Cursor cursor, Map<String, Integer> colIdxMap) {
        Workday workday = BeanFactory.newWorkday();
        workday.setId(cursor.getInt(colIdxMap.get(Columns._ID)));
        workday.setName(cursor.getString(colIdxMap.get(Columns.NAME)));
        workday.setDateRuleId(cursor.getInt(colIdxMap.get(Columns.DATERULEID)));
        workday.setLocationId(cursor.getInt(colIdxMap.get(Columns.LOCATIONID)));
        workday.setActived(ActivedEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.ACTIVED))));
        return workday;
    }

    private ContentValues mapWorkday2ContentValues(Workday workday) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME, workday.getName());
        values.put(Columns.DATERULEID, workday.getDateRuleId());
        values.put(Columns.LOCATIONID, workday.getLocationId());
        values.put(Columns.ACTIVED, workday.isActived() ? ActivedEnum.TRUE : ActivedEnum.FALSE);
        return values;
    }

}
