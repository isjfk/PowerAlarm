/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.bean;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.isjfk.android.rac.RegularAlarmDataProvider;

/**
 * 工作日。
 * 一个位置只能创建一个工作日。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-19
 */
public class Workday implements Parcelable {

    public static final Uri CONTENT_URI = RegularAlarmDataProvider.WORKDAY_URI;
    public static final String TABLENAME = "workday";

    public static class Columns implements BaseColumns {

        /**
         * 工作日名称。
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * 日期规则ID。
         * <P>Type: INTEGER</P>
         */
        public static final String DATERULEID = "dateRuleId";

        /**
         * 位置ID。
         * <P>Type: INTEGER</P>
         */
        public static final String LOCATIONID = "locationId";

        /**
         * 是否在活动状态。
         * 如果当前所在的地区不在工作日的生效地区，则工作日处于非活动状态。<br/>
         * 处于非活动状态的工作日不生效。
         * <P>Type: INTEGER</P>
         */
        public static final String ACTIVED = "actived";

        /** columns to query, null means all columns. */
        public static final String[] QUERY_COLUMNS = null;

        /** columns when query size. */
        public static final String[] QUERY_SIZE = new String[] {
            "count(*)"
        };

        /** id selection. */
        public static final String WHERE_ID = _ID + "=?";

        /** workdayId and locationId selection. */
        public static final String WHERE_LOCID = _ID + "!=? AND " + LOCATIONID + "=?";

        /** default sort order when query. */
        public static final String SORT_ORDER = _ID + " ASC";

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
        dest.writeValue(dateRuleId);
        dest.writeValue(locationId);
        dest.writeValue(actived);
        dest.writeValue(dateRuleList);
        dest.writeValue(location);
    }

    public static final Parcelable.Creator<Workday> CREATOR = new Parcelable.Creator<Workday>() {
        @SuppressWarnings("unchecked")
        public Workday createFromParcel(Parcel in) {
            Workday workday = new Workday();
            workday.id = (Integer) in.readValue(null);
            workday.name = (String) in.readValue(null);
            workday.dateRuleId = (Integer) in.readValue(null);
            workday.locationId = (Integer) in.readValue(null);
            workday.actived = (Boolean) in.readValue(null);
            workday.dateRuleList = (List<DateRule>) in.readValue(DateRule.class.getClassLoader());
            workday.location = (Location) in.readValue(Location.class.getClassLoader());
            return workday;
        }
        public Workday[] newArray(int size) {
            return new Workday[size];
        }
    };

    /** 工作日ID */
    private int id;

    /** 工作日名称 */
    private String name;

    /** 工作日对应的日期规则ID */
    private int dateRuleId;

    /** 工作日对应的位置ID */
    private int locationId;

    /**
     * 是否在活动状态。
     * 如果当前所在的地区不在工作日的生效地区，则工作日处于非活动状态。<br/>
     * 处于非活动状态的工作日不生效。
     */
    private boolean actived;

    /** 工作日对应的日期规则 */
    private List<DateRule> dateRuleList;

    /** 工作日对应的位置 */
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

    public int getDateRuleId() {
        return dateRuleId;
    }

    public void setDateRuleId(int dateRuleId) {
        this.dateRuleId = dateRuleId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
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

}
