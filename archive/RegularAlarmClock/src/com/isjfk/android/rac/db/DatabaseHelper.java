/**
 * (C) Copyright InfiniteSpace Studio, 2011-$2011. All rights reserved.
 */
package com.isjfk.android.rac.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.util.JavaUtil;

/**
 * 数据库工具类。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-13
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "rac.db";

    /** 数据库实现类名中的版本号数字长度 */
    private static final int DB_VERSION_LENGTH = 4;

    private static int latestDBVersion = -1;

    private Context context;

    /**
     * 创建数据库工具类实例。
     *
     * @param context Android上下文
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, getLatestDBVersion());
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        getDatabase(getLatestDBVersion()).create(context, db);
    }

    /**
     * {@inheritDoc}
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        Log.d("begin upgrade database from version " + oldVersion + " to " + currentVersion);

        for (int i = oldVersion; i < currentVersion; i++) {
            getDatabase(i + 1).upgrade(context, db);
        }

        Log.d("end upgrade database from version " + oldVersion + " to " + currentVersion);
    }

    /**
     * {@inheritDoc}
     * @see android.database.sqlite.SQLiteOpenHelper#onDowngrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("begin downgrade database from version " + oldVersion + " to " + newVersion);

        for (int i = newVersion; i > oldVersion; i--) {
            getDatabase(i).downgrade(context, db);
        }

        Log.d("end downgrade database from version " + oldVersion + " to " + newVersion);
    }

    private Database getDatabase(int version) {
        Class<Database> dbClazz = getDatabaseClazz(version);
        if (dbClazz == null) {
            String errMsg = "error find database class of version " + version;
            Log.e(errMsg);
            throw new RACException(errMsg);
        }

        try {
            return dbClazz.newInstance();
        } catch (Exception e) {
            String errMsg = "error create database class of version " + version;
            Log.e(errMsg, e);
            throw new RACException(errMsg, e);
        }
    }

    private static int getLatestDBVersion() {
        if (latestDBVersion == -1) {
            int version = 1;
            while (getDatabaseClazz(version) != null) {
                version++;
            }

            latestDBVersion = version - 1;
            Log.i("latest database version is " + latestDBVersion);
        }

        return latestDBVersion;
    }

    @SuppressWarnings("unchecked")
    private static Class<Database> getDatabaseClazz(int version) {
        String versionStr = JavaUtil.paddingHead(String.valueOf(version), DB_VERSION_LENGTH, '0');
        String clazzName = DatabaseHelper.class.getPackage().getName() + ".DatabaseV" + versionStr;
        try {
            return (Class<Database>) Class.forName(clazzName);
        } catch (Exception e) {
            return null;
        }
    }

}
