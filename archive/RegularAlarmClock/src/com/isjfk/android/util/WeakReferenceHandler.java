/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.util;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * 弱引用Handler。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-8-26
 */
public abstract class WeakReferenceHandler<T> extends Handler {

    protected WeakReference<T> ref;

    /**
     * 构造弱引用Handler。
     *
     * @param context 弱引用上下文
     */
    public WeakReferenceHandler(T context) {
        super();
        this.ref = new WeakReference<T>(context);
    }

    /**
     * {@inheritDoc}
     * @see android.os.Handler#handleMessage(android.os.Message)
     */
    public void handleMessage(Message msg) {
        T context = ref.get();
        if (context == null) {
            return;
        }

        onMessage(context, msg);
    }

    /**
     * 消息处理方法。
     *
     * @param context 弱引用上下文
     * @param msg 消息
     */
    public abstract void onMessage(T context, Message msg);

}
