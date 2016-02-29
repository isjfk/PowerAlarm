/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.callback;

import android.content.Context;

/**
 * 连接到RegularAlarmDataService后的回调接口。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-5
 */
public interface ConnectionCallback {

    /**
     * 连接到RegularAlarmDataService后的回调方法。
     *
     * @param context 客户端上下文
     */
    public void onConnected(Context context);

    /**
     * 意外断开连接到RegularAlarmDataService后的回调方法。
     *
     * @param context 客户端上下文
     */
    public void onDisconnected(Context context);

}
