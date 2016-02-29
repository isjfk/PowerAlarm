/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.util.JavaUtil;

/**
 * 日期规则Dao。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-31
 */
public class DateRuleDao {

    /**
     * 查询一组日期规则。
     *
     * @param resolver ContentResolver
     * @param gid 日期规则组ID
     * @return 日期规则列表，如果不存在返回空List
     */
    public List<DateRule> query(ContentResolver resolver, int gid) {
        List<DateRule> dateRuleList = new ArrayList<DateRule>();

        Cursor cursor = resolver.query(
                DateRule.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                Columns.WHERE_GID,
                new String[] { String.valueOf(gid) },
                Columns.SORT_ORDER);

        if (cursor != null) {
            try {
                int gidIndex = cursor.getColumnIndex(Columns.GID);
                int gindexIndex = cursor.getColumnIndex(Columns.GINDEX);
                int emodeIndex = cursor.getColumnIndex(Columns.EMODE);
                int ruleTypeIndex = cursor.getColumnIndex(Columns.RULETYPE);
                int ruleIndex = cursor.getColumnIndex(Columns.RULE);

                while (cursor.moveToNext()) {
                    DateRule dateRule = BeanFactory.newDateRule(cursor.getInt(ruleTypeIndex));
                    dateRule.setGid(cursor.getInt(gidIndex));
                    dateRule.setGindex(cursor.getInt(gindexIndex));
                    dateRule.setEMode(cursor.getInt(emodeIndex));
                    dateRule.setRule(cursor.getString(ruleIndex));
                    dateRuleList.add(dateRule);
                }
            } finally {
                cursor.close();
            }
        }

        return dateRuleList;
    }

    /**
     * 增加一组日期规则。
     *
     * @param resolver ContentResolver
     * @param dateRuleList 日期规则
     * @return 增加的日期规则组ID。0表示日期规则列表为空；-1表示增加失败
     */
    public int add(ContentResolver resolver, List<DateRule> dateRuleList) {
        return add(resolver, null, dateRuleList);
    }

    /**
     * 增加一组日期规则。
     *
     * @param resolver ContentResolver
     * @param gid 日期规则组ID，如果为null或EMPTY_GID则自动生成
     * @param dateRuleList 日期规则
     * @return 增加的日期规则组ID。0表示日期规则列表为空；-1表示增加失败
     */
    public int add(ContentResolver resolver, Integer gid, List<DateRule> dateRuleList) {
        if (JavaUtil.isEmpty(dateRuleList)) {
            return DateRule.EMPTY_GID;
        }

        if ((gid == null) || (gid == DateRule.EMPTY_GID)) {
            gid = getNextGid(resolver);
        }
        if (gid != -1) {
            for (int i = 0; i < dateRuleList.size(); i++) {
                DateRule dateRule = dateRuleList.get(i);
                dateRule.setGid(gid);
                dateRule.setGindex(i);

                ContentValues values = new ContentValues();
                values.put(Columns.GID, dateRule.getGid());
                values.put(Columns.GINDEX, dateRule.getGindex());
                values.put(Columns.EMODE, dateRule.getEMode());
                values.put(Columns.RULETYPE, dateRule.getRuleType());
                values.put(Columns.RULE, dateRule.getRule());

                resolver.insert(DateRule.CONTENT_URI, values);
            }
        }

        return gid;
    }

    /**
     * 删除一组日期规则。
     *
     * @param resolver ContentResolver
     * @param gid 日期规则组ID
     * @return 删除的日期规则数量
     */
    public int delete(ContentResolver resolver, int gid) {
        return resolver.delete(
                DateRule.CONTENT_URI,
                Columns.WHERE_GID,
                new String[] { String.valueOf(gid) });
    }

    /**
     * 获取一组日期规则的总数。
     *
     * @param resolver ContentResolver
     * @param gid 日期规则组ID
     * @return 日期规则组总数，如果无法获取则返回-1
     */
    public int getSize(ContentResolver resolver, int gid) {
        Cursor cursor = resolver.query(
                DateRule.CONTENT_URI,
                Columns.QUERY_SIZE,
                Columns.WHERE_GID,
                new String[] { String.valueOf(gid) },
                null);
        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return cursor.getInt(0);
            } finally {
                cursor.close();
            }
        }

        Log.e("error get daterule size");
        return -1;
    }

    /**
     * 获取下一个日期规则组ID。
     *
     * @param resolver ContentResolver
     * @return 下一个日期规则组ID，如果无法获取则返回-1
     */
    private int getNextGid(ContentResolver resolver) {
        Cursor cursor = resolver.query(
                DateRule.CONTENT_URI,
                Columns.QUERY_MAXGID,
                null,
                null,
                null);
        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return cursor.getInt(0) + 1;
            } finally {
                cursor.close();
            }
        }

        Log.e("error get next daterule gid");
        return -1;
    }

}
