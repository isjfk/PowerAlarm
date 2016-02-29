/**
 * (C) Copyright InfiniteSpace Studio, 2011-$2011. All rights reserved.
 */
package com.isjfk.android.rac;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.Config;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.LocArea;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.db.DatabaseHelper;

/**
 * 规则闹钟数据源。
 *
 * @author Jimmy F.Klarke
 * @version 1.0, 2011-07-12
 */
public class RegularAlarmDataProvider extends ContentProvider {

    private static final String PROVIDER_PKG = "com.isjfk.android.rac.regularalarmdataprovider";

    public static final Uri CONFIG_URI = Uri.parse("content://" + PROVIDER_PKG + "/config");
    public static final Uri LOCATION_URI = Uri.parse("content://" + PROVIDER_PKG + "/location");
    public static final Uri DATERULE_URI = Uri.parse("content://" + PROVIDER_PKG + "/daterule");
    public static final Uri WORKDAY_URI = Uri.parse("content://" + PROVIDER_PKG + "/workday");
    public static final Uri ALARMRULE_URI = Uri.parse("content://" + PROVIDER_PKG + "/alarmrule");
    public static final Uri SCHEDULE_URI = Uri.parse("content://" + PROVIDER_PKG + "/schedule");

    static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static final int CONFIGS = 1;
    static final int CONFIG_ID = 2;
    static final int LOCATIONS = 101;
    static final int LOCATION_ID = 102;
    static final int DATERULES = 201;
    static final int DATERULE_ID = 202;
    static final int WORKDAYS = 301;
    static final int WORKDAY_ID = 302;
    static final int ALARMRULES = 401;
    static final int ALARMRULE_ID = 402;
    static final int SCHEDULES = 501;
    static final int SCHEDULE_ID = 502;

    static {
        sURLMatcher.addURI(PROVIDER_PKG, "config", CONFIGS);
        sURLMatcher.addURI(PROVIDER_PKG, "config/#", CONFIG_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "location", LOCATIONS);
        sURLMatcher.addURI(PROVIDER_PKG, "location/#", LOCATION_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "location", LOCATIONS);
        sURLMatcher.addURI(PROVIDER_PKG, "location/#", LOCATION_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "daterule", DATERULES);
        sURLMatcher.addURI(PROVIDER_PKG, "daterule/#", DATERULE_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "workday", WORKDAYS);
        sURLMatcher.addURI(PROVIDER_PKG, "workday/#", WORKDAY_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "alarmrule", ALARMRULES);
        sURLMatcher.addURI(PROVIDER_PKG, "alarmrule/#", ALARMRULE_ID);
        sURLMatcher.addURI(PROVIDER_PKG, "schedule", SCHEDULES);
        sURLMatcher.addURI(PROVIDER_PKG, "schedule/#", SCHEDULE_ID);
    }

    private SQLiteOpenHelper dbHelper;

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {
        switch (sURLMatcher.match(uri)) {
        case CONFIGS:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.config";
        case CONFIG_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.config";
        case LOCATIONS:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.location";
        case LOCATION_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.location";
        case DATERULES:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.daterule";
        case DATERULE_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.daterule";
        case WORKDAYS:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.workday";
        case WORKDAY_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.workday";
        case ALARMRULES:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.alarmrule";
        case ALARMRULE_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.alarmrule";
        case SCHEDULES:
            return "vnd.android.cursor.dir/vnd.isjfk.cac.schedule";
        case SCHEDULE_ID:
            return "vnd.android.cursor.item/vnd.isjfk.cac.schedule";
        default:
            Log.e("unknown uri: " + uri);
            throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        boolean isSingle = false;
        switch (sURLMatcher.match(uri)) {
        case CONFIG_ID:
            isSingle = true;
        case CONFIGS:
            query.setTables(Config.TABLENAME);
            break;
        case LOCATION_ID:
            isSingle = true;
        case LOCATIONS:
            query.setTables(LocArea.TABLENAME);
            break;
        case DATERULE_ID:
            isSingle = true;
        case DATERULES:
            query.setTables(DateRule.TABLENAME);
            break;
        case WORKDAY_ID:
            isSingle = true;
        case WORKDAYS:
            query.setTables(Workday.TABLENAME);
            break;
        case ALARMRULE_ID:
            isSingle = true;
        case ALARMRULES:
            query.setTables(AlarmRule.TABLENAME);
            break;
        case SCHEDULE_ID:
            isSingle = true;
        case SCHEDULES:
            query.setTables(Schedule.TABLENAME);
            break;
        default:
            Log.e("unknown uri: " + uri);
            throw new IllegalArgumentException("unknown uri: " + uri);
        }

        if (isSingle) {
            query.appendWhere("_id=");
            query.appendWhere(uri.getPathSegments().get(1));
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = query.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (cursor == null) {
            Log.d("query data failed, uri: " + uri);
        } else {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        String tableName = null;
        String nullHack = null;
        Uri contentUri = null;
        switch (sURLMatcher.match(uri)) {
        case CONFIGS:
            tableName = Config.TABLENAME;
            nullHack = Config.Columns.VALUE;
            contentUri = Config.CONTENT_URI;
            break;
        case LOCATIONS:
            tableName = LocArea.TABLENAME;
            nullHack = LocArea.Columns.ADDR9;
            contentUri = LocArea.CONTENT_URI;
            break;
        case DATERULES:
            tableName = DateRule.TABLENAME;
            nullHack = DateRule.Columns.RULE;
            contentUri = DateRule.CONTENT_URI;
            break;
        case WORKDAYS:
            tableName = Workday.TABLENAME;
            nullHack = Workday.Columns.NAME;
            contentUri = Workday.CONTENT_URI;
            break;
        case ALARMRULES:
            tableName = AlarmRule.TABLENAME;
            nullHack = AlarmRule.Columns.NAME;
            contentUri = AlarmRule.CONTENT_URI;
            break;
        case SCHEDULES:
            tableName = Schedule.TABLENAME;
            contentUri = Schedule.CONTENT_URI;
            break;
        default:
            Log.e("unknown uri: " + uri);
            throw new IllegalArgumentException("unknown uri: " + uri);
        }

        ContentValues values = new ContentValues(initialValues);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, nullHack, values);
        if (rowId < 0) {
            throw new SQLException("insert data failed, uri: " + uri);
        }
        Log.d("add data success, rowId: " + rowId + ", uri: " + uri);

        Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = null;
        boolean isSingle = false;
        switch (sURLMatcher.match(uri)) {
        case CONFIG_ID:
            isSingle = true;
        case CONFIGS:
            tableName = Config.TABLENAME;
            break;
        case LOCATION_ID:
            isSingle = true;
        case LOCATIONS:
            tableName = LocArea.TABLENAME;
            break;
        case DATERULE_ID:
            isSingle = true;
        case DATERULES:
            tableName = DateRule.TABLENAME;
            break;
        case WORKDAY_ID:
            isSingle = true;
        case WORKDAYS:
            tableName = Workday.TABLENAME;
            break;
        case ALARMRULE_ID:
            isSingle = true;
        case ALARMRULES:
            tableName = AlarmRule.TABLENAME;
            break;
        case SCHEDULE_ID:
            isSingle = true;
        case SCHEDULES:
            tableName = Schedule.TABLENAME;
            break;
        default:
            Log.e("unknown uri: " + uri);
            throw new IllegalArgumentException("unknown uri: " + uri);
        }

        if (isSingle) {
            long rowId = ContentUris.parseId(uri);
            if (TextUtils.isEmpty(selection)) {
                selection = "_id=" + rowId;
            } else {
                selection = "_id=" + rowId + " AND (" + selection + ")";
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(tableName, values, selection, selectionArgs);
        Log.d("update data success, count: " + count + ", uri: " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * {@inheritDoc}
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = null;
        boolean isSingle = false;
        switch (sURLMatcher.match(uri)) {
        case CONFIG_ID:
            isSingle = true;
        case CONFIGS:
            tableName = Config.TABLENAME;
            break;
        case LOCATION_ID:
            isSingle = true;
        case LOCATIONS:
            tableName = LocArea.TABLENAME;
            break;
        case DATERULE_ID:
            isSingle = true;
        case DATERULES:
            tableName = DateRule.TABLENAME;
            break;
        case WORKDAY_ID:
            isSingle = true;
        case WORKDAYS:
            tableName = Workday.TABLENAME;
            break;
        case ALARMRULE_ID:
            isSingle = true;
        case ALARMRULES:
            tableName = AlarmRule.TABLENAME;
            break;
        case SCHEDULE_ID:
            isSingle = true;
        case SCHEDULES:
            tableName = Schedule.TABLENAME;
            break;
        default:
            Log.e("unknown uri: " + uri);
            throw new IllegalArgumentException("unknown uri: " + uri);
        }

        if (isSingle) {
            long rowId = ContentUris.parseId(uri);
            if (TextUtils.isEmpty(selection)) {
                selection = "_id=" + rowId;
            } else {
                selection = "_id=" + rowId + " AND (" + selection + ")";
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(tableName, selection, selectionArgs);
        Log.d("delete data success, count: " + count + ", uri: " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
