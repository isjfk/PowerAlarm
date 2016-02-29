/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.rac.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.Log;

/**
 * 数据库实现。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-10-21
 */
public class DatabaseV0003 implements Database {

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.db.Database#create(android.content.Context, android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void create(Context context, SQLiteDatabase db) {
        db.execSQL("CREATE TABLE config ("
                + "_id INTEGER PRIMARY KEY, "
                + "key TEXT, "
                + "value TEXT);");

        db.execSQL("CREATE TABLE location ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "type INTEGER, "
                + "areaType INTEGER, "
                + "longitude REAL, "
                + "latitude REAL, "
                + "radius INTEGER, "
                + "radiusUnit TEXT, "
                + "addrSize INTEGER, "
                + "addr0 TEXT, "
                + "addr1 TEXT, "
                + "addr2 TEXT, "
                + "addr3 TEXT, "
                + "addr4 TEXT, "
                + "addr5 TEXT, "
                + "addr6 TEXT, "
                + "addr7 TEXT, "
                + "addr8 TEXT, "
                + "addr9 TEXT);");

        String insertLocation = "INSERT INTO location "
                + "(_id, name, type, areaType)"
                + " VALUES ";
        db.execSQL(insertLocation + "(1, 'All Location', 0, 0);");
        db.execSQL(insertLocation + "(2, 'Other Location', 1, 0);");

        db.execSQL("CREATE TABLE daterule ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "gid INTEGER, "
                + "gindex INTEGER, "
                + "emode INTEGER, "
                + "ruleType INTEGER, "
                + "rule TEXT);");

        String insertDateRule = "INSERT INTO daterule "
                + "(_id, gid, gindex, emode, ruleType, rule)"
                + " VALUES ";
        db.execSQL(insertDateRule + "(1, 1, 0, 1, 1, '2,3,4,5,6');");

        db.execSQL("CREATE TABLE workday ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "dateRuleId INTEGER, "
                + "locationId INTEGER, "
                + "actived INTEGER);");

        String defaultWorkdayName = context.getResources().getString(R.string.workdayDefaultName);
        String insertWorkday = "INSERT INTO workday "
                + "(name, dateRuleId, locationId, actived)"
                + " VALUES ";
        db.execSQL(insertWorkday + "('" + defaultWorkdayName + "', 1, 1, 1);");

        db.execSQL("CREATE TABLE alarmrule ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "time TEXT, "
                + "dateRuleId INTEGER, "
                + "delAfterDismiss INTEGER, "
                + "complyWorkday INTEGER, "
                + "locationId INTEGER, "
                + "ring INTEGER, "
                + "ringtone TEXT, "
                + "vibrate INTEGER, "
                + "enabled INTEGER, "
                + "actived INTEGER);");

        db.execSQL("CREATE TABLE schedule ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "dateTime TEXT, "
                + "alarmRuleId INTEGER, "
                + "enabled INTEGER, "
                + "actived INTEGER, "
                + "timeoutTimes INTEGER);");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.db.Database#drop(android.content.Context, android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void drop(Context context, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS config");
        db.execSQL("DROP TABLE IF EXISTS location");
        db.execSQL("DROP TABLE IF EXISTS daterule");
        db.execSQL("DROP TABLE IF EXISTS workday");
        db.execSQL("DROP TABLE IF EXISTS alarmrule");
        db.execSQL("DROP TABLE IF EXISTS schedule");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.db.Database#upgrade(android.content.Context, android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void upgrade(Context context, SQLiteDatabase db) {
        db.execSQL("ALTER TABLE alarmrule ADD COLUMN delAfterDismiss");
        db.execSQL("UPDATE alarmrule SET delAfterDismiss=0");
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.db.Database#downgrade(android.content.Context, android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void downgrade(Context context, SQLiteDatabase db) {
        Log.e("recreate database due to downgrade not supported");

        drop(context, db);
        create(context, db);
    }

}
