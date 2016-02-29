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

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.AlarmRule.Columns;
import com.isjfk.android.rac.bean.AlarmRule.Columns.ActivedEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.ComplyWorkdayEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.DelAfterExpiredEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.EnabledEnum;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.common.BeanFactory;

/**
 * 闹铃规则Dao。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class AlarmRuleDao {

    /**
     * 查询所有闹铃规则。
     *
     * @param resolver ContentResolver
     * @return 闹铃规则列表，如果不存在返回空List
     */
    public List<AlarmRule> queryAll(ContentResolver resolver) {
        List<AlarmRule> alarmRuleList = new ArrayList<AlarmRule>();

        Cursor cursor = resolver.query(
                AlarmRule.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                null,
                null,
                Columns.SORT_ORDER);

        if (cursor != null) {
            try {
                Map<String, Integer> colIdxMap = getColumnIndexMap(cursor);
                while (cursor.moveToNext()) {
                    alarmRuleList.add(mapCursor2AlarmRule(cursor, colIdxMap));
                }
            } finally {
                cursor.close();
            }
        }

        return alarmRuleList;
    }

    /**
     * 查询闹铃规则。
     *
     * @param resolver ContentResolver
     * @param id 闹铃规则ID
     * @return 闹铃规则，如果不存在返回null
     */
    public AlarmRule query(ContentResolver resolver, int id) {
        Cursor cursor = resolver.query(
                AlarmRule.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) },
                Columns.SORT_ORDER);

        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return mapCursor2AlarmRule(cursor, getColumnIndexMap(cursor));
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * 增加闹铃规则。
     *
     * @param resolver ContentResolver
     * @param alarmRule 闹铃规则
     * @return 增加的闹铃规则ID
     */
    public int add(ContentResolver resolver, AlarmRule alarmRule) {
        Uri uri = resolver.insert(AlarmRule.CONTENT_URI, mapAlarmRule2ContentValues(alarmRule));
        return (int) ContentUris.parseId(uri);
    }

    /**
     * 删除闹铃规则。
     *
     * @param resolver ContentResolver
     * @param id 闹铃规则ID
     * @return 删除的闹铃规则数量
     */
    public int delete(ContentResolver resolver, int id) {
        return resolver.delete(
                AlarmRule.CONTENT_URI,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 修改闹铃规则。
     *
     * @param resolver ContentResolver
     * @param alarmRule 闹铃规则
     * @return 修改的闹铃规则数量
     */
    public int update(ContentResolver resolver, AlarmRule alarmRule) {
        return resolver.update(
                AlarmRule.CONTENT_URI,
                mapAlarmRule2ContentValues(alarmRule),
                Columns.WHERE_ID,
                new String[] { String.valueOf(alarmRule.getId()) });
    }

    /**
     * 修改闹铃规则是否启用状态。
     *
     * @param resolver ContentResolver
     * @param id 闹铃规则ID
     * @param enabled 闹铃规则是否启用
     * @return 修改的闹铃规则数目
     */
    public int updateEnabled(ContentResolver resolver, int id, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(Columns.ENABLED, enabled ? EnabledEnum.TRUE : EnabledEnum.FALSE);

        return resolver.update(
                AlarmRule.CONTENT_URI,
                values,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    /**
     * 修改闹铃规则是否活动状态。
     *
     * @param resolver ContentResolver
     * @param id 闹铃规则ID
     * @param actived 闹铃规则是否活动
     * @return 修改的闹铃规则数目
     */
    public int updateActived(ContentResolver resolver, int id, boolean actived) {
        ContentValues values = new ContentValues();
        values.put(Columns.ACTIVED, actived ? ActivedEnum.TRUE : ActivedEnum.FALSE);

        return resolver.update(
                AlarmRule.CONTENT_URI,
                values,
                Columns.WHERE_ID,
                new String[] { String.valueOf(id) });
    }

    private Map<String, Integer> getColumnIndexMap(Cursor cursor) {
        Map<String, Integer> colIdxMap = new HashMap<String, Integer>();
        colIdxMap.put(Columns._ID, cursor.getColumnIndex(Columns._ID));
        colIdxMap.put(Columns.NAME, cursor.getColumnIndex(Columns.NAME));
        colIdxMap.put(Columns.TIME, cursor.getColumnIndex(Columns.TIME));
        colIdxMap.put(Columns.DATERULEID, cursor.getColumnIndex(Columns.DATERULEID));
        colIdxMap.put(Columns.DELAFTEREXPIRED, cursor.getColumnIndex(Columns.DELAFTEREXPIRED));
        colIdxMap.put(Columns.COMPLYWORKDAY, cursor.getColumnIndex(Columns.COMPLYWORKDAY));
        colIdxMap.put(Columns.LOCATIONID, cursor.getColumnIndex(Columns.LOCATIONID));
        colIdxMap.put(Columns.RING, cursor.getColumnIndex(Columns.RING));
        colIdxMap.put(Columns.RINGTONE, cursor.getColumnIndex(Columns.RINGTONE));
        colIdxMap.put(Columns.VIBRATE, cursor.getColumnIndex(Columns.VIBRATE));
        colIdxMap.put(Columns.ALARMTIME, cursor.getColumnIndex(Columns.ALARMTIME));
        colIdxMap.put(Columns.ENABLED, cursor.getColumnIndex(Columns.ENABLED));
        colIdxMap.put(Columns.ACTIVED, cursor.getColumnIndex(Columns.ACTIVED));
        return colIdxMap;
    }

    private AlarmRule mapCursor2AlarmRule(Cursor cursor, Map<String, Integer> colIdxMap) {
        AlarmRule alarmRule = BeanFactory.newAlarmRule();
        alarmRule.setId(cursor.getInt(colIdxMap.get(Columns._ID)));
        alarmRule.setName(cursor.getString(colIdxMap.get(Columns.NAME)));
        alarmRule.setTime(cursor.getString(colIdxMap.get(Columns.TIME)));
        alarmRule.setDateRuleId(cursor.getInt(colIdxMap.get(Columns.DATERULEID)));
        alarmRule.setDelAfterExpired(
                DelAfterExpiredEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.DELAFTEREXPIRED))));
        alarmRule.setComplyWorkday(
                ComplyWorkdayEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.COMPLYWORKDAY))));
        alarmRule.setLocationId(cursor.getInt(colIdxMap.get(Columns.LOCATIONID)));
        alarmRule.setRing(cursor.getInt(colIdxMap.get(Columns.RING)));
        alarmRule.setRingtone(RingtoneConfig.decode(cursor.getString(colIdxMap.get(Columns.RINGTONE))));
        alarmRule.setVibrate(cursor.getInt(colIdxMap.get(Columns.VIBRATE)));
        alarmRule.setAlarmTime(cursor.getInt(colIdxMap.get(Columns.ALARMTIME)));
        alarmRule.setEnabled(EnabledEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.ENABLED))));
        alarmRule.setActived(ActivedEnum.TRUE.equals(cursor.getInt(colIdxMap.get(Columns.ACTIVED))));
        return alarmRule;
    }

    private ContentValues mapAlarmRule2ContentValues(AlarmRule alarmRule) {
        ContentValues values = new ContentValues();
        values.put(Columns.NAME, alarmRule.getName());
        values.put(Columns.TIME, alarmRule.getTime());
        values.put(Columns.DATERULEID, alarmRule.getDateRuleId());
        values.put(Columns.DELAFTEREXPIRED,
                alarmRule.isDelAfterExpired() ? DelAfterExpiredEnum.TRUE : DelAfterExpiredEnum.FALSE);
        values.put(Columns.COMPLYWORKDAY,
                alarmRule.isComplyWorkday() ? ComplyWorkdayEnum.TRUE : ComplyWorkdayEnum.FALSE);
        values.put(Columns.LOCATIONID, alarmRule.getLocationId());
        values.put(Columns.RING, alarmRule.getRing());
        values.put(Columns.RINGTONE, RingtoneConfig.encode(alarmRule.getRingtone()));
        values.put(Columns.VIBRATE, alarmRule.getVibrate());
        values.put(Columns.ALARMTIME, alarmRule.getAlarmTime());
        values.put(Columns.ENABLED, alarmRule.isEnabled() ? EnabledEnum.TRUE : EnabledEnum.FALSE);
        values.put(Columns.ACTIVED, alarmRule.isActived() ? ActivedEnum.TRUE : ActivedEnum.FALSE);
        return values;
    }

}
