/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.rac.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库接口。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-10-21
 */
public interface Database {

    /**
     * 创建数据库。
     *
     * @param context 上下文
     * @param db 数据库实例
     */
    void create(Context context, SQLiteDatabase db);

    /**
     * 删除数据库。
     *
     * @param context 上下文
     * @param db 数据库实例
     */
    void drop(Context context, SQLiteDatabase db);

    /**
     * 将数据库从上一个版本升级到当前版本。
     *
     * @param context 上下文
     * @param db 数据库实例
     */
    void upgrade(Context context, SQLiteDatabase db);

    /**
     * 将数据库从当前版本降级到上一个版本。
     *
     * @param context 上下文
     * @param db 数据库实例
     */
    void downgrade(Context context, SQLiteDatabase db);

}
