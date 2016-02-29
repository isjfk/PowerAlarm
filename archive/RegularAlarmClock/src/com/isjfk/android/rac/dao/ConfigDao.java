/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.isjfk.android.rac.bean.Config;
import com.isjfk.android.rac.bean.Config.Columns;

/**
 * 配置项Dao。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-3
 */
public class ConfigDao {

    /**
     * 获取配置项value。
     *
     * @param resolver ContentResolver
     * @param key 配置项key
     * @param defaultValue 默认值
     * @return 配置项value，如果不存在返回defaultValue
     */
    public String get(ContentResolver resolver, String key, String defaultValue) {
        Cursor cursor = resolver.query(
                Config.CONTENT_URI,
                Columns.QUERY_COLUMNS,
                Columns.WHERE_KEY,
                new String[] { key },
                Columns.SORT_ORDER);

        if ((cursor != null) && cursor.moveToNext()) {
            try {
                return cursor.getString(cursor.getColumnIndex(Columns.VALUE));
            } finally {
                cursor.close();
            }
        }

        return defaultValue;
    }

    /**
     * 增加配置项。
     *
     * @param resolver ContentResolver
     * @param key 配置项key
     * @param value 配置项value
     */
    public void set(ContentResolver resolver, String key, String value) {
        remove(resolver, key);

        ContentValues values = new ContentValues();
        values.put(Columns.KEY, key);
        values.put(Columns.VALUE, value);
        resolver.insert(Config.CONTENT_URI, values);
    }

    /**
     * 删除配置项。
     *
     * @param resolver ContentResolver
     * @param key 配置项key
     */
    public void remove(ContentResolver resolver, String key) {
        resolver.delete(
                Config.CONTENT_URI,
                Columns.WHERE_KEY,
                new String[] { key });
    }

}
