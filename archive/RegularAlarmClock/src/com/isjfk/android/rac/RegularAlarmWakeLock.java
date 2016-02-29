/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import android.content.Context;
import android.os.PowerManager;

import com.isjfk.android.rac.common.Log;

/**
 * CPU唤醒锁。
 * 由AlarmManager触发的广播事件，Android系统保证在时间的onReceive()方法中手机保持运行状态。
 * 但离开onReceive()方法后手机可能马上进入休眠模式，造成onReceive()方法中启动的Service无法执行。
 * CPU唤醒锁用于保证在响铃期间手机一直处于唤醒状态。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-2-25
 */
class RegularAlarmWakeLock {

    private static PowerManager.WakeLock wakeLock = null;

    /**
     * 获取CPU唤醒锁。
     *
     * @param context Context
     */
    public static synchronized void acquire(Context context) {
        if (wakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                Log.TAG);
        wakeLock.acquire();

        Log.d("cpu wake lock acquired");
    }

    /**
     * 释放CPU唤醒锁。
     */
    public static synchronized void release() {
        if (wakeLock == null) {
            return;
        }

        wakeLock.release();
        wakeLock = null;

        Log.d("cpu wake lock released");
    }

}
