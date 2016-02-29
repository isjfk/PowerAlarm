/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import java.util.Calendar;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.isjfk.android.rac.RegularAlarmDataProvider;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 闹铃日程。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public class Schedule implements Parcelable {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.SCHEDULE_URI;
    public static final String TABLENAME = "schedule";

    public static class Columns implements BaseColumns {

        /**
         * 闹铃名称。
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * 响铃日期和时间。
         * <P>Type: TEXT</P>
         * 格式：yyyy-MM-dd HH:mm。如“2011-01-05 09:10”表示2011年1月5日9点10分。
         */
        public static final String DATETIME = "dateTime";

        /**
         * 对应的闹铃规则ID。
         * <P>Type: INTEGER</P>
         */
        public static final String ALARMRULEID = "alarmRuleId";

        /**
         * 是否启用此日程。
         * <P>Type: INTEGER</P>
         */
        public static final String ENABLED = "enabled";

        /**
         * 是否在活动状态。
         * 如果对应的闹铃规则不在活动状态，或闹铃日程不在工作日内，则闹铃日程处于非活动状态。<br/>
         * 处于非活动状态的闹铃日程不生效。
         * <P>Type: INTEGER</P>
         */
        public static final String ACTIVED = "actived";

        /**
         * 超时次数。
         * 响铃时的超时次数，如果超过一定次数用户仍然无操作则自动停止响铃。
         * <P>Type: INTEGER</P>
         */
        public static final String TIMEOUTTIMES = "timeoutTimes";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = null;

        /** default sort order when query. */
        public static final String SORT_ORDER = DATETIME + " ASC, " + ALARMRULEID + " ASC";

        /** default sort order when query. */
        public static final String WHERE_ID = _ID + "=?";

        public interface EnabledEnum {
            Integer FALSE = 0;
            Integer TRUE = 1;
        }

        public interface ActivedEnum {
            Integer FALSE = 0;
            Integer TRUE = 1;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(dateTime);
        dest.writeValue(alarmRuleId);
        dest.writeValue(enabled);
        dest.writeValue(actived);
        dest.writeValue(timeoutTimes);
    }

    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        public Schedule createFromParcel(Parcel in) {
            Schedule schedule = new Schedule();
            schedule.id = (Integer) in.readValue(null);
            schedule.name = (String) in.readValue(null);
            schedule.dateTime = (String) in.readValue(null);
            schedule.alarmRuleId = (Integer) in.readValue(null);
            schedule.enabled = (Boolean) in.readValue(null);
            schedule.actived = (Boolean) in.readValue(null);
            schedule.timeoutTimes = (Integer) in.readValue(null);
            return schedule;
        }
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    /** 闹铃日程ID */
    private int id;

    /** 闹铃名称 */
    private String name;

    /** 响铃日期和时间 */
    private String dateTime;

    /** 对应的闹铃规则ID */
    private int alarmRuleId;

    /** 是否启用此日程 */
    private boolean enabled;

    /**
     * 是否在活动状态。
     * 如果对应的闹铃规则不在活动状态，或闹铃日程不在工作日内，则闹铃日程处于非活动状态。<br/>
     * 处于非活动状态的闹铃日程不生效。
     */
    private boolean actived;

    /**
     * 超时次数。
     * 响铃时的超时次数，如果超过一定次数用户仍然无操作则自动停止响铃。
     */
    private int timeoutTimes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getAlarmRuleId() {
        return alarmRuleId;
    }

    public void setAlarmRuleId(int alarmRuleId) {
        this.alarmRuleId = alarmRuleId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }

    public int getTimeoutTimes() {
        return timeoutTimes;
    }

    public void setTimeoutTimes(int timeoutTimes) {
        this.timeoutTimes = timeoutTimes;
    }

    public Calendar getTime() {
        Calendar time = RACTimeUtil.stdParseDateTime(dateTime);
        if (time == null) {
            time = Calendar.getInstance();
        }
        return time;
    }

    public void setTime(Calendar date) {
        this.dateTime = RACTimeUtil.stdFormatDateTime(date);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Schedule) {
            Schedule that = (Schedule) o;
            if (this.id == that.id) {
                return true;
            }
            if ((this.alarmRuleId == that.alarmRuleId) && JavaUtil.equals(this.dateTime, that.dateTime)) {
                return true;
            }
        }
        return false;
    }

}
