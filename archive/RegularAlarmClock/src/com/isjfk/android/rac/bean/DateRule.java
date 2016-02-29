/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import java.util.Calendar;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.isjfk.android.rac.RegularAlarmDataProvider;
import com.isjfk.android.rac.bean.DateRule.Columns.EModeEnum;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.common.BeanFactory;

/**
 * 日期规则。
 * 一个规则可能包含多条记录，称为一个规则组。同组规则的编号相同，索引不同。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public abstract class DateRule implements Parcelable {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.DATERULE_URI;
    public static final String TABLENAME = "daterule";
    public static final Integer EMPTY_GID = 0;

    public static class Columns implements BaseColumns {

        /**
         * 规则组ID。
         * <P>Type: INTEGER</P>
         */
        public static final String GID = "gid";

        /**
         * 规则组内索引，从0开始。
         * <P>Type: INTEGER</P>
         */
        public static final String GINDEX = "gindex";

        /**
         * 规则组内生效方式。
         * <P>Type: INTEGER</P>
         */
        public static final String EMODE = "emode";

        /**
         * 规则类型。
         * <P>Type: INTEGER</P>
         */
        public static final String RULETYPE = "ruleType";

        /**
         * 规则定义。
         * <P>Type: TEXT</P>
         * 根据ruletype的不同值，含义如下：
         */
        public static final String RULE = "rule";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = null;

        /** columns when query max gid. */
        public static final String[] QUERY_MAXGID = new String[] {
            "max(" + GID + ")"
        };

        /** columns when query size. */
        public static final String[] QUERY_SIZE = new String[] {
            "count(*)"
        };

        /** gid selection. */
        public static final String WHERE_GID = GID + "=?";

        /** default sort order when query. */
        public static final String SORT_ORDER = GID + ", " + GINDEX + " ASC";

        public interface EModeEnum {
            Integer WITHIN = 0;
            Integer ADD = 1;
            Integer SUBTRACT = 2;
        }

        public interface RuleTypeEnum {
            Integer EveryDay = 0;
            Integer DayOfWeek = 1;
            Integer DayOfMonth = 2;
            Integer DayOfWeekInMonth = 3;
            Integer RevDayOfWeekInMonth = 4;
            Integer DayMonthRange = 5;
            Integer DayMonthYearRange = 6;
            Integer AlarmXOffYDays = 7;
            Integer DayMonthYearList = 8;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(ruleType);
        dest.writeValue(gid);
        dest.writeValue(gindex);
        dest.writeValue(eMode);
        dest.writeValue(rule);
    }

    public static final Parcelable.Creator<DateRule> CREATOR = new Parcelable.Creator<DateRule>() {
        public DateRule createFromParcel(Parcel in) {
            DateRule dateRule = BeanFactory.newDateRule((Integer) in.readValue(null));
            dateRule.gid = (Integer) in.readValue(null);
            dateRule.gindex = (Integer) in.readValue(null);
            dateRule.eMode = (Integer) in.readValue(null);
            dateRule.rule = (String) in.readValue(null);
            return dateRule;
        }
        public DateRule[] newArray(int size) {
            return new DateRule[size];
        }
    };

    /**
     * 获取规则的文本描述。
     *
     * @param res 应用资源
     * @return 规则的文本描述
     */
    public abstract String desc(Resources res);

    /**
     * 判断对于指定的日期，规则是否生效。
     *
     * @param date 日期
     * @return 如果生效返回true，否则返回false
     */
    public abstract boolean test(Calendar date);

    /**
     * 获取日期规则失效日期。
     * 不考虑eMode对日期规则失效日期的影响。
     *
     * @return 日期规则失效日期，如果永远不失效则返回null
     */
    public abstract DayMonthYear getExpireDate();

    /** 规则组ID */
    private int gid;

    /** 规则组内索引，从0开始 */
    private int gindex;

    /**
     * 规则组内生效方式。
     * 组内第一条规则默认为1。
     * <li>0：与（在之前的日期中减去本规则没有的日期）</li>
     * <li>1：或（在之前的日期中加上本规则中的日期）</li>
     * <li>2：与非（在之前的日期中减去本规则中的日期）</li>
     * <li>3：或非（在之前的日期中加上本规则没有的日期）</li>
     */
    private int eMode = EModeEnum.ADD;

    /**
     * 规则类型。
     * <li>0：每天生效</li>
     * <li>1：按周生效</li>
     * <li>2：按月生效</li>
     * <li>3：每月正数第X周的星期Y</li>
     * <li>4：每月倒数第X周的星期Y</li>
     * <li>5：指定月日范围</li>
     * <li>6：指定日期范围</li>
     * <li>7：响铃X天停止Y天</li>
     */
    private int ruleType;

    /**
     * 规则定义。
     * 根据ruletype的不同值，含义如下：
     * <li>1：存放每周中生效的星期，1-7表示星期天到星期六，用逗号分隔。如2,3,4,5,6表示星期一到星期五</li>
     * <li>2：存放每月中生效的日期，用逗号分隔。如1,2,5表示每月1、2、5号</li>
     * <li>3：存放具体规则，如1|3,7表示每月正数第1个周的星期二和星期六</li>
     * <li>4：存放具体规则，如1|3,7表示每月倒数第1个周的星期二和星期六</li>
     * <li>5：存放月日范围，如05.00|02.02表示1月5日到3月2日</li>
     * <li>6：存放日期范围，如05.00.1983|02.02.1984</li>
     */
    private String rule;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getGindex() {
        return gindex;
    }

    public void setGindex(int gindex) {
        this.gindex = gindex;
    }

    public int getEMode() {
        return eMode;
    }

    public void setEMode(int eMode) {
        this.eMode = eMode;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

}
