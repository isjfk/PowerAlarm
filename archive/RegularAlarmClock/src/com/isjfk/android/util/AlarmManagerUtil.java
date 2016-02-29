/**
 * (C) Copyright InfiniteSpace Studio, 2011-2014. All rights reserved.
 */
package com.isjfk.android.util;

import com.isjfk.android.rac.common.Log;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

/**
 * AlarmManager工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2014年7月19日
 */
public class AlarmManagerUtil {

    /**
     * 调用AlarmManager的setExact()方法。
     * 对于API Level小于19的安卓系统，调用set方法。否则调用setExact()方法。
     *
     * @param am AlarmManager实例
     * @param type Alarm类型
     * @param triggerAtMillis 触发时间
     * @param operation 触发时的动作
     */
    @SuppressLint("NewApi")
    public static void setExact(AlarmManager am, int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT < 19) {
            am.set(type, triggerAtMillis, operation);
            Log.e("set()");
        } else {
            am.setExact(type, triggerAtMillis, operation);
            Log.e("setExact()");
        }
    }

}
