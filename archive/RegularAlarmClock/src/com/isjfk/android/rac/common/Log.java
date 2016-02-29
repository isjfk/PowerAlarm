/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

/**
 * 日志工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class Log {

    public final static String TAG = "RegularAlarmClock";

    public static int v(String msg) {
        return android.util.Log.v(TAG, msg);
    }

    public static int v(String msg, Throwable e) {
        return android.util.Log.v(TAG, msg, e);
    }

    public static int d(String msg) {
        return android.util.Log.d(TAG, msg);
    }

    public static int d(String msg, Throwable e) {
        return android.util.Log.d(TAG, msg, e);
    }

    public static int i(String msg) {
        return android.util.Log.i(TAG, msg);
    }

    public static int i(String msg, Throwable e) {
        return android.util.Log.i(TAG, msg, e);
    }

    public static int w(String msg) {
        return android.util.Log.w(TAG, msg);
    }

    public static int w(String msg, Throwable e) {
        return android.util.Log.w(TAG, msg, e);
    }

    public static int e(String msg) {
        return android.util.Log.e(TAG, msg);
    }

    public static int e(String msg, Throwable e) {
        return android.util.Log.e(TAG, msg, e);
    }

}
