/**
 * (C) Copyright InfiniteSpace Studio, 2011-2013. All rights reserved.
 */
package com.isjfk.android.util;

import android.content.Intent;
import android.os.Parcel;

/**
 * Android工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2013-4-22
 */
public class AndroidUtil {

    private static String intentExtraPrefix;

    /**
     * 向intent放入属性。
     *
     * @param intent intent
     * @param key 属性key
     * @param value 属性value
     */
    public static void putExtra(Intent intent, String key, Object value) {
        intent.putExtra(getKey(key), marshall(value));
    }

    /**
     * 从intent获取属性。
     *
     * @param intent intent
     * @param key 属性key
     * @return 属性value
     */
    public static <T> T getExtra(Intent intent, String key) {
        return unmarshall(intent.getByteArrayExtra(getKey(key)));
    }

    /**
     * 从intent获取属性。
     *
     * @param intent intent
     * @param key 属性key
     * @param defaultValue 属性默认value
     * @return 属性value，如果为null则返回默认value
     */
    public static <T> T getExtra(Intent intent, String key, T defaultValue) {
        T value = unmarshall(intent.getByteArrayExtra(getKey(key)));
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * 将对象序列化为数据。
     *
     * @param value 对象
     * @return 数据，如果对象为null则返回null
     */
    public static byte[] marshall(Object value) {
        if (value == null) {
            return null;
        }

        Parcel parcel = Parcel.obtain();
        try {
            parcel.writeValue(value);
            return parcel.marshall();
        } finally {
            parcel.recycle();
        }
    }

    /**
     * 将数据反序列化为对象。
     *
     * @param data 数据
     * @return 对象，如果数据为null则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshall(byte[] data) {
        if (data == null) {
            return null;
        }

        Parcel parcel = Parcel.obtain();
        try {
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return (T) parcel.readValue(AndroidUtil.class.getClassLoader());
        } finally {
            parcel.recycle();
        }
    }

    /**
     * 设置Intent Extra前缀。
     *
     * @param intentExtraPrefix Intent Extra前缀
     */
    public static void setIntentExtraPrefix(String intentExtraPrefix) {
        AndroidUtil.intentExtraPrefix = intentExtraPrefix;
    }

    private static String getKey(String key) {
        if (JavaUtil.isEmpty(intentExtraPrefix)) {
            return key;
        }

        return intentExtraPrefix + "." + key;
    }

}
