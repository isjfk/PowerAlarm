/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.isjfk.android.rac.RegularAlarmDataProvider;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.rule.RuleUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 闹铃规则。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public class AlarmRule implements Parcelable, Cloneable {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.ALARMRULE_URI;
    public static final String TABLENAME = "alarmrule";

    public static class Columns implements BaseColumns {

        /**
         * 闹铃名称。
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * 闹铃时间。
         * <P>Type: TEXT</P>
         */
        public static final String TIME = "time";

        /**
         * 闹铃规则对应的日期规则ID。
         * <P>Type: INTEGER</P>
         */
        public static final String DATERULEID = "dateRuleId";

        /**
         * 闹铃规则过期后是否删除此规则。
         * <P>Type: INTEGER</P>
         * <li>0：响铃后不删除</li>
         * <li>1：响铃后删除</li>
         */
        public static final String DELAFTEREXPIRED = "delAfterDismiss";

        /**
         * 是否遵循工作日，仅在dateRule不是OneTime时有效。
         * <P>Type: INTEGER</P>
         * <li>0：不遵循工作日，所有日期都响铃</li>
         * <li>1：遵循工作日，仅在工作日响铃</li>
         */
        public static final String COMPLYWORKDAY = "complyWorkday";

        /**
         * 闹铃规则对应的位置ID。
         * <P>Type: INTEGER</P>
         */
        public static final String LOCATIONID = "locationId";

        /**
         * 是否响铃。
         * <P>Type: INTEGER</P>
         * <li>0：使用首选项中的默认值</li>
         * <li>1：响铃</li>
         * <li>2：不响铃</li>
         */
        public static final String RING = "ring";

        /**
         * 响铃使用的铃声。
         * <P>Type: TEXT</P>
         */
        public static final String RINGTONE = "ringtone";

        /**
         * 是否振动。
         * <P>Type: INTEGER</P>
         * <li>0：使用首选项中的默认值</li>
         * <li>1：振动</li>
         * <li>2：不振动</li>
         */
        public static final String VIBRATE = "vibrate";

        /**
         * 响铃时间。
         * <P>Type: INTEGER</P>
         * <li>-1：使用首选项中的默认值</li>
         * <li>0：与铃声长度相同</li>
         * <li>其他：响铃具体时间。单位：秒</li>
         */
        public static final String ALARMTIME = "alarmTime";

        /**
         * 闹铃规则是否启用。
         * <P>Type: INTEGER</P>
         * <li>0：未启用</li>
         * <li>1：启用</li>
         */
        public static final String ENABLED = "enabled";

        /**
         * 是否在活动状态。
         * 如果当前所在的地区不在闹铃规则的生效地区，则此规则处于非活动状态。<br/>
         * 处于非活动状态的闹铃日程不生效。
         * <P>Type: INTEGER</P>
         */
        public static final String ACTIVED = "actived";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = null;

        /** id selection. */
        public static final String WHERE_ID = _ID + "=?";

        /** default sort order when query. */
        public static final String SORT_ORDER = _ID + " ASC";

        public interface DelAfterExpiredEnum {
            Integer FALSE = 0;
            Integer TRUE = 1;
        }

        public interface ComplyWorkdayEnum {
            Integer FALSE = 0;
            Integer TRUE = 1;
        }

        public interface RingEnum {
            Integer DEFAULT = 0;
            Integer RING = 1;
            Integer NORING = 2;
        }

        public interface VibrateEnum {
            Integer DEFAULT = 0;
            Integer VIBRATE = 1;
            Integer NOVIBRATE = 2;
        }

        public interface AlarmTimeEnum {
            Integer DEFAULT = 0;
            Integer RINGTONE_LENGTH = -1;
        }

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
        dest.writeValue(time);
        dest.writeValue(dateRuleId);
        dest.writeValue(delAfterExpired);
        dest.writeValue(complyWorkday);
        dest.writeValue(locationId);
        dest.writeValue(ring);
        dest.writeValue(ringtone);
        dest.writeValue(vibrate);
        dest.writeValue(alarmTime);
        dest.writeValue(enabled);
        dest.writeValue(actived);
        dest.writeValue(dateRuleList);
        dest.writeValue(location);
    }

    public static final Parcelable.Creator<AlarmRule> CREATOR = new Parcelable.Creator<AlarmRule>() {
        @SuppressWarnings("unchecked")
        public AlarmRule createFromParcel(Parcel in) {
            AlarmRule alarmRule = new AlarmRule();
            alarmRule.id = (Integer) in.readValue(null);
            alarmRule.name = (String) in.readValue(null);
            alarmRule.time = (String) in.readValue(null);
            alarmRule.dateRuleId = (Integer) in.readValue(null);
            alarmRule.delAfterExpired = (Boolean) in.readValue(null);
            alarmRule.complyWorkday = (Boolean) in.readValue(null);
            alarmRule.locationId = (Integer) in.readValue(null);
            alarmRule.ring = (Integer) in.readValue(null);
            alarmRule.ringtone = (RingtoneConfig) in.readValue(RingtoneConfig.class.getClassLoader());
            alarmRule.vibrate = (Integer) in.readValue(null);
            alarmRule.alarmTime = (Integer) in.readValue(null);
            alarmRule.enabled = (Boolean) in.readValue(null);
            alarmRule.actived = (Boolean) in.readValue(null);
            alarmRule.dateRuleList = (List<DateRule>) in.readValue(DateRule.class.getClassLoader());
            alarmRule.location = (Location) in.readValue(Location.class.getClassLoader());
            return alarmRule;
        }
        public AlarmRule[] newArray(int size) {
            return new AlarmRule[size];
        }
    };

    @Override
    public AlarmRule clone() {
        Parcel parcel = Parcel.obtain();
        try {
            this.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            return CREATOR.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }

    /** 闹铃规则ID */
    private int id;

    /** 闹铃名称 */
    private String name;

    /**
     * 闹铃时间。
     * <P>格式：HH:mm。如“09:10”表示9点10分。</P>
     */
    private String time;

    /** 闹铃规则对应的日期规则ID */
    private int dateRuleId;

    /** 响铃后是否删除此闹铃规则 */
    private boolean delAfterExpired;

    /** 闹铃规则是否遵循工作日。 */
    private boolean complyWorkday;

    /** 闹铃规则对应的位置ID */
    private int locationId;

    /**
     * 是否响铃。
     * <li>0：根据配置项决定</li>
     * <li>1：不响铃</li>
     * <li>2：响铃</li>
     */
    private int ring;

    /** 响铃使用的铃声 */
    private RingtoneConfig ringtone;

    /**
     * 是否振动。
     * <li>0：根据配置项决定</li>
     * <li>1：不振动</li>
     * <li>2：振动</li>
     */
    private int vibrate;

    /**
     * 响铃时间。
     * <li>0：根据配置项决定</li>
     * <li>-1：与铃声长度相同</li>
     * <li>其他：具体长度。单位：秒</li>
     */
    private int alarmTime;

    /** 闹铃规则是否启用 */
    private boolean enabled;

    /**
     * 是否在活动状态。
     * 如果当前所在的地区不在闹铃规则的生效地区，则此规则处于非活动状态。<br/>
     * 处于非活动状态的闹铃日程不生效。
     */
    private boolean actived;

    /** 闹铃规则对应的日期规则 */
    private List<DateRule> dateRuleList;

    /** 闹铃规则对应的位置 */
    private Location location;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDateRuleId() {
        return dateRuleId;
    }

    public void setDateRuleId(int dateRuleId) {
        this.dateRuleId = dateRuleId;
    }

    public boolean isDelAfterExpired() {
        return delAfterExpired;
    }

    public void setDelAfterExpired(boolean delAfterExpired) {
        this.delAfterExpired = delAfterExpired;
    }

    public boolean isComplyWorkday() {
        return complyWorkday;
    }

    public void setComplyWorkday(boolean complyWorkday) {
        this.complyWorkday = complyWorkday;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getRing() {
        return ring;
    }

    public void setRing(int ring) {
        this.ring = ring;
    }

    public RingtoneConfig getRingtone() {
        if (ringtone == null) {
            ringtone = BeanFactory.newRingtoneConfig();
        }
        return ringtone;
    }

    public void setRingtone(RingtoneConfig ringtone) {
        this.ringtone = ringtone;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public int getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(int alarmTime) {
        this.alarmTime = alarmTime;
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

    public Calendar getTimeAsCalendar() {
        if (JavaUtil.isEmpty(time)) {
            return null;
        }
        return RACTimeUtil.stdParseTime(time);
    }

    public int getHour() {
        List<Integer> timeList = JavaUtil.toIntList(time, ":");
        if (timeList.size() < 1) {
            return 0;
        }
        return timeList.get(0);
    }

    public void setHour(int hour) {
        setTime(hour, getMinute());
    }

    public int getMinute() {
        List<Integer> timeList = JavaUtil.toIntList(time, ":");
        if (timeList.size() < 2) {
            return 0;
        }
        return timeList.get(1);
    }

    public void setMinute(int minute) {
        setTime(getHour(), minute);
    }

    public void setTime(int hour, int minute) {
        setTime(JavaUtil.paddingHead(String.valueOf(hour), 2, '0')
                + ":"
                + JavaUtil.paddingHead(String.valueOf(minute), 2, '0'));
    }

    public List<DateRule> getDateRuleList() {
        if (dateRuleList == null) {
            dateRuleList = new ArrayList<DateRule>();
        }
        return dateRuleList;
    }

    public void setDateRuleList(List<DateRule> dateRuleList) {
        this.dateRuleList = dateRuleList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isOneTime() {
        return RuleUtil.isOneTime(getDateRuleList());
    }

    public boolean isExpireable() {
        return RuleUtil.isExpireable(getDateRuleList());
    }

    public boolean isExpired() {
        DayMonthYear expireDate = getExpireDate();
        return (expireDate != null) && (expireDate.compareTo(Calendar.getInstance()) <= 0);
    }

    public DayMonthYear getExpireDate() {
        return RuleUtil.getExpireDate(getDateRuleList());
    }

}
