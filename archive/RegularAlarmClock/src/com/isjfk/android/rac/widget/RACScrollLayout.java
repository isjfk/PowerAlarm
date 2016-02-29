/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.isjfk.android.rac.ScrollLayout;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.timeview.TimeView;

/**
 * RAC定制DateSlider的ScrollLayout控件。
 * 滑动时请求父ScrollView不要处理滑动事件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-17
 */
public class RACScrollLayout extends ScrollLayout {

    /** 日期控件滑动监听器 */
    private OnScrollChangeListener onScrollChangeListener;

    public RACScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 获取包含指定时间的时间控件其表示时间段的中间时间。
     *
     * @param time 时间控件需要包含的时间
     * @return 包含指定时间的时间控件其表示时间段的中间时间，如果找不到包含指定时间的时间控件则返回null
     */
    public Calendar getTimeOfViewCenter(Calendar time) {
        long timeMillis = time.getTimeInMillis();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof TimeView) {
                TimeView timeView = (TimeView) child;

                if ((timeMillis >= timeView.getStartTime()) && (timeMillis <= timeView.getEndTime())) {
                    long halfTimeMillis = (timeView.getEndTime() - timeView.getStartTime()) / 2;
                    long centerTimeMillis = timeView.getStartTime() + halfTimeMillis;

                    if (centerTimeMillis != timeMillis) {
                        Calendar centerTime = (Calendar) time.clone();
                        centerTime.setTimeInMillis(centerTimeMillis);
                        return centerTime;
                    }
                }
            }
        }

        return null;
    }

    public OnScrollChangeListener getOnScrollChangeListener() {
        return onScrollChangeListener;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onDateScrollListener) {
        this.onScrollChangeListener = onDateScrollListener;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.ScrollLayout#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            RACUtil.requestMotionEvent(this);
            fireOnScrollBegin();
        } else if (ev.getAction() != MotionEvent.ACTION_MOVE) {
            RACUtil.releaseMotionEvent(this);
        }

        boolean result = super.onTouchEvent(ev);

        if ((ev.getAction() == MotionEvent.ACTION_UP) || (ev.getAction() == MotionEvent.ACTION_CANCEL)) {
            fireOnScrollEnd();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.ScrollLayout#computeScroll()
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        fireOnScrollFling();
    }

    private void fireOnScrollBegin() {
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollBegin(this);
        }
    }

    private void fireOnScrollEnd() {
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollEnd(this);
        }
    }

    private void fireOnScrollFling() {
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollFling(this);
        }
    }

    /**
     * 日期控件滑动监听器。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-26
     */
    public static interface OnScrollChangeListener {

        /**
         * 日期控件滑动开始。
         *
         * @param racScrollLayout 触发事件的日期控件
         */
        void onScrollBegin(RACScrollLayout racScrollLayout);

        /**
         * 日期控件滑动结束。
         *
         * @param racScrollLayout 触发事件的日期控件
         */
        void onScrollEnd(RACScrollLayout racScrollLayout);

        /**
         * 日期控件滑动结束后的惯性移动。
         *
         * @param racScrollLayout 触发事件的日期控件
         */
        void onScrollFling(RACScrollLayout racScrollLayout);

    }

}
