/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.vibrate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Vibrator;

/**
 * 震动播放器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-5
 */
public class VibratePlayer {

    private static final long VIBRATE_FIRST_INTERVAL_MS = 100;
    private static final long VIBRATE_INTERVAL_MS = 1000;
    private static final long VIBRATE_TIME_START_MS = 50;
    private static final long VIBRATE_TIME_MS = 150;
    private static final long VIBRATE_GAP_MS = 50;

    private Vibrator vibrator;

    private boolean playing = false;

    /** 淡入时间，单位“秒” */
    private Integer fadeInTime = null;

    public VibratePlayer(Context context) {
        super();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public Integer getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(Integer fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public synchronized boolean isPlaying() {
        return playing;
    }

    public synchronized void play() {
        if (playing) {
            stop();
        }

        List<Long> vibratePattern = new ArrayList<Long>();

        addVibrate(vibratePattern, VIBRATE_FIRST_INTERVAL_MS);
        while (addVibrate(vibratePattern, VIBRATE_INTERVAL_MS));

        vibrator.vibrate(toArray(vibratePattern), vibratePattern.size() - 4);

        playing = true;
    }

    public synchronized void stop() {
        if (!playing) {
            return;
        }

        vibrator.cancel();

        playing = false;
    }

    private boolean addVibrate(List<Long> vibratePattern, long pauseTimeMs) {
        vibratePattern.add(pauseTimeMs);

        long totalTimeMs = calcTotalTimeMs(vibratePattern);
        long fadeInTimeMs = (fadeInTime != null) ? (fadeInTime * 1000) : 0;
        if (totalTimeMs < fadeInTimeMs) {
            long vibrateTimeScalaMs = VIBRATE_TIME_MS - VIBRATE_TIME_START_MS;
            long vibrateTimeMs = (long) ((totalTimeMs / (double) fadeInTimeMs) * vibrateTimeScalaMs) + VIBRATE_TIME_START_MS;

            vibratePattern.add(vibrateTimeMs);
            vibratePattern.add(VIBRATE_GAP_MS);
            vibratePattern.add(vibrateTimeMs);

            return true;
        } else {
            vibratePattern.add(VIBRATE_TIME_MS);
            vibratePattern.add(VIBRATE_GAP_MS);
            vibratePattern.add(VIBRATE_TIME_MS);

            return false;
        }
    }

    private long calcTotalTimeMs(List<Long> vibratePattern) {
        long totalTimeMs = 0l;
        for (Long length : vibratePattern) {
            totalTimeMs += length;
        }
        return totalTimeMs;
    }

    private long[] toArray(List<Long> vibratePattern) {
        long[] array = new long[vibratePattern.size()];
        for (int i = 0; i < vibratePattern.size(); i++) {
            array[i] = vibratePattern.get(i);
        }
        return array;
    }

}
