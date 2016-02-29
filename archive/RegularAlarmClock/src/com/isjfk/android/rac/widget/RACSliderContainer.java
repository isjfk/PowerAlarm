/**
 * (C) Copyright InfiniteSpace Studio, 2011-2012. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.isjfk.android.rac.SliderContainer;
import com.isjfk.android.rac.widget.RACScrollLayout.OnScrollChangeListener;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * RAC定制DateSlider的ScrollLayout控件。
 * 滑动结束后将最后一个控件的时间标签居中。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-8-26
 */
public class RACSliderContainer extends SliderContainer {

    /** 将当前时间控件移动到中间消息 */
    private static final int MSG_CENTER_TIME = 1;

    /** 将当前时间控件移动到中间消息触发延迟时间 */
    private static final int CENTER_TIME_DELAY = 200;

    /** 最后一个日期滑动控件 */
    private RACScrollLayout lastScrollLayout = null;

    /** 延迟将当前时间控件移动到中间Handler */
    private Handler delayCenterTimeHandler = new DelayCenterTimeHandler(this);

    /**
     * 延迟将当前时间控件移动到中间Handler。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-26
     */
    private static class DelayCenterTimeHandler extends WeakReferenceHandler<RACSliderContainer> {
        public DelayCenterTimeHandler(RACSliderContainer context) {
            super(context);
        }

        @Override
        public void onMessage(RACSliderContainer context, Message msg) {
            switch (msg.what) {
            case MSG_CENTER_TIME:
                context.moveSelectedTimeInCenter();
                break;
            }
        }
    }

    public RACSliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 调整当前选中的时间控件位置，使其显示在中间。
     */
    private void moveSelectedTimeInCenter() {
        if (lastScrollLayout == null) {
            return;
        }

        Calendar currTime = getTime();
        if (currTime == null) {
            return;
        }

        Calendar centerTime = lastScrollLayout.getTimeOfViewCenter(currTime);
        if (centerTime == null) {
            return;
        }

        super.setTime(centerTime);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.SliderContainer#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof RACScrollLayout) {
                RACScrollLayout scrollLayout = (RACScrollLayout) child;
                lastScrollLayout = scrollLayout;

                scrollLayout.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollBegin(RACScrollLayout racScrollLayout) {
                        deleteFireScrollFinsh();
                    }
                    @Override
                    public void onScrollEnd(RACScrollLayout racScrollLayout) {
                        scheduleFireScrollFinsh();
                    }
                    @Override
                    public void onScrollFling(RACScrollLayout racScrollLayout) {
                        if (racScrollLayout == lastScrollLayout) {
                            postponeFireScrollFinsh();
                        }
                    }
                });
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.SliderContainer#setTime(java.util.Calendar)
     */
    @Override
    public void setTime(Calendar calendar) {
        super.setTime(calendar);
        scheduleFireScrollFinsh();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.SliderContainer#setMinuteInterval(int)
     */
    @Override
    public void setMinuteInterval(int minInterval) {
        super.setMinuteInterval(minInterval);
        scheduleFireScrollFinsh();
    }

    private void scheduleFireScrollFinsh() {
        if (delayCenterTimeHandler.hasMessages(MSG_CENTER_TIME)) {
            delayCenterTimeHandler.removeMessages(MSG_CENTER_TIME);
        }
        delayCenterTimeHandler.sendEmptyMessageDelayed(MSG_CENTER_TIME, CENTER_TIME_DELAY);
    }

    private void postponeFireScrollFinsh() {
        if (delayCenterTimeHandler.hasMessages(MSG_CENTER_TIME)) {
            delayCenterTimeHandler.removeMessages(MSG_CENTER_TIME);
            delayCenterTimeHandler.sendEmptyMessageDelayed(MSG_CENTER_TIME, CENTER_TIME_DELAY);
        }
    }

    private void deleteFireScrollFinsh() {
        if (delayCenterTimeHandler.hasMessages(MSG_CENTER_TIME)) {
            delayCenterTimeHandler.removeMessages(MSG_CENTER_TIME);
        }
    }

}
