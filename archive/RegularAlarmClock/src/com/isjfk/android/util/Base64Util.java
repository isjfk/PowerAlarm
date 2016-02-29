/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.util;

import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACException;

/**
 * Base64工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-4-4
 */
public class Base64Util {

    private static final int FLAGS = Base64.NO_WRAP | Base64.NO_PADDING;
    private static final String ENCODING = "US-ASCII";

    /**
     * 对ASCII字符串进行Base64编码。
     *
     * @param str ASCII字符串
     * @return Base64编码后的字符串，如果str为空则返回null
     */
    public static String encode(String str) {
        if (JavaUtil.isEmpty(str)) {
            return null;
        }

        try {
            return new String(Base64.encode(str.getBytes(ENCODING), FLAGS), ENCODING);
        } catch (Exception e) {
            Log.e("error encode base64 string", e);
            throw new RACException(e);
        }
    }

    /**
     * 对Base64编码的ASCII字符串进行解码。
     *
     * @param base64Str base64编码的ASCII字符串
     * @return ASCII字符串，如果base64Str为空则返回null
     */
    public static String decode(String base64Str) {
        if (JavaUtil.isEmpty(base64Str)) {
            return null;
        }

        try {
            return new String(Base64.decode(base64Str.getBytes(ENCODING), FLAGS), ENCODING);
        } catch (Exception e) {
            Log.e("error decode base64 string", e);
            throw new RACException(e);
        }
    }

}
