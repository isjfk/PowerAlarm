/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import com.isjfk.android.rac.RegularAlarmDataProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 位置区域。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public class LocArea {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.LOCATION_URI;
    public static final String TABLENAME = "location";

    // 系统内置的全部位置的ID
    public static final Integer DEFAULT_ALL_LOC_ID = 1;

    // 系统内置的其它位置的ID
    public static final Integer DEFAULT_OTHER_LOC_ID = 2;

    public static class Columns implements BaseColumns {

        /**
         * 位置名称。
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * 位置类型。
         * <li>0：所有位置（系统保留）</li>
         * <li>1：其它位置（系统保留，匹配不到用户指定的位置时生效）</li>
         * <li>2：指定坐标和半径范围</li>
         * <li>3：指定地址</li>
         * <P>Type: INTEGER</P>
         */
        public static final String TYPE = "type";

        /**
         * 区域类型。
         * <li>0：正常区域</li>
         * <li>1：休息区，进入时关闭所有闹钟，退出后恢复</li>
         * <P>Type: INTEGER</P>
         */
        public static final String AREATYPE = "areaType";

        /**
         * 经度。
         * type为2时生效。
         * <P>Type: REAL</P>
         */
        public static final String LONGITUDE = "longitude";

        /**
         * 维度。
         * type为2时生效。
         * <P>Type: REAL</P>
         */
        public static final String LATITUDE = "latitude";

        /**
         * 半径。
         * type为2时生效。
         * <P>Type: INTEGER</P>
         */
        public static final String RADIUS = "radius";

        /**
         * 半径单位。
         * type为2时生效。
         * <P>Type: TEXT</P>
         */
        public static final String RADIUSUNIT = "radiusUnit";

        /**
         * 地址数量。
         * type为3时生效。
         * <P>Type: INTEGER</P>
         */
        public static final String ADDRSIZE = "addrSize";

        /**
         * 地址0。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR0 = "addr0";

        /**
         * 地址1。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR1 = "addr1";

        /**
         * 地址2。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR2 = "addr2";

        /**
         * 地址3。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR3 = "addr3";

        /**
         * 地址4。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR4 = "addr4";

        /**
         * 地址5。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR5 = "addr5";

        /**
         * 地址6。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR6 = "addr6";

        /**
         * 地址7。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR7 = "addr7";

        /**
         * 地址8。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR8 = "addr8";

        /**
         * 地址9。
         * type为3时生效。
         * <P>Type: TEXT</P>
         */
        public static final String ADDR9 = "addr9";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = null;

        /** default sort order when query. */
        public static final String SORT_ORDER = _ID + " ASC";

    }

}
