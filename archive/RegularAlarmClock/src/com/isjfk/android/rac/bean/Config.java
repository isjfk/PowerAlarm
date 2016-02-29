/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import android.net.Uri;
import android.provider.BaseColumns;

import com.isjfk.android.rac.RegularAlarmDataProvider;

/**
 * 配置项。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public class Config {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.CONFIG_URI;
    public static final String TABLENAME = "config";

    public static class Columns implements BaseColumns {

        /**
         * 配置项Key。
         * <P>Type: TEXT</P>
         */
        public static final String KEY = "key";

        /**
         * 配置项值。
         * <P>Type: TEXT</P>
         */
        public static final String VALUE = "value";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = {
            _ID, KEY, VALUE
        };

        /** id selection. */
        public static final String WHERE_KEY = KEY + "=?";

        /** default sort order when query. */
        public static final String SORT_ORDER = _ID + " ASC";

        public interface KeyEnum {
            String ScheduleEndDate = "scheduleEndDate";
        }

    }

}
