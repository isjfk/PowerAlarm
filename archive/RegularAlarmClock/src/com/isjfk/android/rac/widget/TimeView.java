/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;

/**
 * 时间控件，支持12小时制和24小时制。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-10-24
 */
public class TimeView extends LinearLayout {

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置时间。
     *
     * @param time 时间
     */
    public void setTime(Calendar time) {
        if (time == null) {
            Log.e("error set time null");
            return;
        }

        if (RACContext.isTime24HourFormat()) {
            ((LinearLayout) findViewById(R.id.timeAMPM)).setVisibility(GONE);
        } else {
            ((LinearLayout) findViewById(R.id.timeAMPM)).setVisibility(VISIBLE);
            if (RACUtil.isAM(time)) {
                ((TextView) findViewById(R.id.timeAM)).setVisibility(VISIBLE);
                ((TextView) findViewById(R.id.timePM)).setVisibility(INVISIBLE);
            } else {
                ((TextView) findViewById(R.id.timeAM)).setVisibility(INVISIBLE);
                ((TextView) findViewById(R.id.timePM)).setVisibility(VISIBLE);
            }
        }
        ((TextView) findViewById(R.id.timeText)).setText(RACTimeUtil.formatTimeOnly(time));
    }

    /**
     * 设置文字颜色。
     *
     * @param color 颜色
     */
    public void setTextColor(int color) {
        ((TextView) findViewById(R.id.timeAM)).setTextColor(color);
        ((TextView) findViewById(R.id.timePM)).setTextColor(color);
        ((TextView) findViewById(R.id.timeText)).setTextColor(color);
    }

    /**
     * 设置文字颜色。
     *
     * @param color 颜色
     */
    public void setTextColor(ColorStateList color) {
        ((TextView) findViewById(R.id.timeAM)).setTextColor(color);
        ((TextView) findViewById(R.id.timePM)).setTextColor(color);
        ((TextView) findViewById(R.id.timeText)).setTextColor(color);
    }

}
