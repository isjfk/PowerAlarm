/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Java语言工具。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-1
 */
public class JavaUtil {

    private static final int MAX_CHAR = 0xFF;

    /**
     * 判断两个字符串是否一致。
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 一致返回true，否则返回false
     */
    public static boolean equals(String str1, String str2) {
        if ((str1 == null) && (str2 == null)) {
            return true;
        } else if (str1 != null) {
            return str1.equals(str2);
        } else if (str2 != null) {
            return str2.equals(str1);
        }
        return false;
    }

    /**
     * 去除字符串首尾的空格。
     *
     * @param str 字符串
     * @return 去除首尾空格的字符串
     */
    public static String trim(String str) {
        if (str != null) {
            return str.trim();
        }
        return null;
    }

    /**
     * 去除字符串首尾的空格。
     * 如果原字符串为null或空字符串，返回空字符串。
     *
     * @param str 字符串
     * @return 去除首尾空格的字符串
     */
    public static String trimToEmpty(String str) {
        if (str != null) {
            return str.trim();
        }
        return "";
    }

    /**
     * 去除字符串首尾的空格。
     * 如果原字符串为null或空字符串，返回null。
     *
     * @param str 字符串
     * @return 去除首尾空格的字符串
     */
    public static String trimToNull(String str) {
        if (str != null) {
            str = str.trim();
        }
        if ((str == null) || "".equals(str)) {
            return null;
        }
        return str;
    }

    /**
     * 判断字符串是否为空。
     *
     * @param str 字符串
     * @return 如果字符串为null或只含有空格、tab字符返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return (str == null) || "".equals(trim(str));
    }

    /**
     * 判断字符串是否不为空。
     *
     * @param str 字符串
     * @return 如果字符串为null或只含有空格、tab字符返回false，否则返回true
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断容器是否为空。
     *
     * @param col 容器
     * @return 如果容器为null或为空返回true，否则返回false
     */
    public static boolean isEmpty(Collection<?> col) {
        if ((col == null) || col.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断容器是否不为空。
     *
     * @param col 容器
     * @return 如果容器不为null且不为空返回true，否则返回false
     */
    public static boolean isNotEmpty(Collection<?> col) {
        return !isEmpty(col);
    }

    /**
     * 判断字符串中是否全都是ascii字符。
     *
     * @param str 字符串
     * @return 全都是ascii字符返回true，否则返回false
     */
    public static boolean isAllAscii(String str) {
        if (isNotEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                int codePoint = str.codePointAt(i);
                if (codePoint > MAX_CHAR) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 将字符串转换为整形数字。
     *
     * @param str 字符串
     * @return 整形数字，如果无法转换返回null
     */
    public static Integer toInteger(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将符号分隔的字符串转换成字符串List。
     *
     * @param sepStr 符号分隔的字符串，如"2,3,4"
     * @param sep 分隔字符，如","
     * @return 字符串List，如果没有数据返回空List
     */
    public static List<String> split(String sepStr, String sep) {
        if (JavaUtil.isEmpty(sepStr)) {
            sepStr = "";
        }
        List<String> strList = new ArrayList<String>();
        String[] intStrArr = sepStr.split(sep);
        for (String part : intStrArr) {
            if (isNotEmpty(part)) {
                strList.add(part);
            }
        }
        return strList;
    }

    /**
     * 将数组转换为List。
     *
     * @param <T> 数据类型
     * @param array 数组
     * @return List
     */
    public static <T> List<T> toList(T[] array) {
        List<T> list = new ArrayList<T>();
        for (T elem : array) {
            list.add(elem);
        }
        return list;
    }

    /**
     * 将符号分隔的数字组成的字符串转换成数字List。
     *
     * @param sepIntStr 符号分隔的数字组成的字符串，如"2,3,4"
     * @param sep 分隔字符，如","
     * @return 数字List，如果没有数据返回空List
     */
    public static List<Integer> toIntList(String sepIntStr, String sep) {
        if (JavaUtil.isEmpty(sepIntStr)) {
            sepIntStr = "";
        }
        List<Integer> intList = new ArrayList<Integer>();
        String[] intStrArr = sepIntStr.split(sep);
        for (String intStr : intStrArr) {
            Integer intVal = JavaUtil.toInteger(intStr);
            if (intVal != null) {
                intList.add(intVal);
            }
        }
        return intList;
    }

    /**
     * 在字符串之前填充字符。
     *
     * @param str 需要填充的字符串
     * @param len 填充后的总长度，如果小于字符串长度则不进行填充
     * @param fill 填充的字符
     * @return 填充后的字符串
     */
    public static String paddingHead(String str, int len, char fill) {
        StringBuilder buf = new StringBuilder();
        while ((buf.length() + str.length()) < len) {
            buf.append(fill);
        }
        return buf.toString() + str;
    }

}
