/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.ringtone.RingtonePlayer;
import com.isjfk.android.rac.ringtone.RingtonePlayer.RingtonePlayerListener;
import com.isjfk.android.rac.tts.TTSPlayer;
import com.isjfk.android.rac.tts.TTSPlayer.TTSPlayerListener;
import com.isjfk.android.rac.vibrate.VibratePlayer;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 闹铃播放器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-5
 */
public class RegularAlarmPlayer {

    private static final int PLAYER_LOW_VOLUME = 1;

    private AudioManager audioManager;

    private Boolean ring = null;
    private Boolean speakAlarmName = null;
    private Boolean vibrate = null;
    private Integer playTime = null;

    private boolean playing = false;

    private RingtonePlayer ringtonePlayer;
    private TTSPlayer ttsPlayer;
    private VibratePlayer vibratePlayer;

    private RingtonePlayerListener ringtonePlayerListener = new RingtonePlayerListener() {
        @Override
        public void onStart() {
        }
        @Override
        public void onStop() {
            stop();
        }
    };

    private TTSPlayerListener ttsPlayerListener = new TTSPlayerListener() {
        @Override
        public void onStartSpeak() {
            ringtonePlayer.setMaxVolume(PLAYER_LOW_VOLUME);
        }
        @Override
        public void onEndSpeak() {
            ringtonePlayer.setMaxVolume(RACContext.getMaxVolume());
        }
    };

    private List<RegularAlarmPlayerListener> listenerList = new ArrayList<RegularAlarmPlayerListener>();

    private static final int MSG_TIMEOUT = 1;
    private Handler timeoutHandler = new TimeoutHandler(this);

    /**
     * 响铃超时Handler。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-12
     */
    private static class TimeoutHandler extends WeakReferenceHandler<RegularAlarmPlayer> {
        public TimeoutHandler(RegularAlarmPlayer service) {
            super(service);
        }

        @Override
        public void onMessage(RegularAlarmPlayer player, Message msg) {
            switch (msg.what) {
            case MSG_TIMEOUT:
                player.stop();
                break;
            }
        }
    }

    public RegularAlarmPlayer(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        ringtonePlayer = new RingtonePlayer(context, audioManager);
        ttsPlayer = new TTSPlayer(context);
        vibratePlayer = new VibratePlayer(context);
    }

    /**
     * 配置闹铃播放器。
     *
     * @param config 铃声配置
     */
    public void configRingtone(RingtoneConfig config) {
        if (config == null) {
            return;
        }

        if (config.isPreference()) {
            setRingtone(RACContext.getGlobalRingtoneConfig().getRingtone());
            setVolume(RACContext.getGlobalRingtoneConfig().getVolume());
            setFadeInTime(RACContext.getGlobalRingtoneConfig().getFadeInTime());
        } else if (config.isDefault()) {
            setRingtone(RACContext.getDefaultRingtone());
            setVolume(config.getVolume());
            setFadeInTime(config.getFadeInTime());
        } else {
            setRingtone(config.getRingtone());
            setVolume(config.getVolume());
            setFadeInTime(config.getFadeInTime());
        }
    }

    public Boolean getRing() {
        return ring;
    }

    public void setRing(Boolean ring) {
        this.ring = ring;
    }

    public Boolean getSpeakAlarmName() {
        return speakAlarmName;
    }

    public void setSpeakAlarmName(Boolean speakAlarmName) {
        this.speakAlarmName = speakAlarmName;
    }

    public Boolean getVibrate() {
        return vibrate;
    }

    public void setVibrate(Boolean vibrate) {
        this.vibrate = vibrate;
    }

    public Integer getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Integer playTime) {
        this.playTime = playTime;
    }

    public Uri getRingtone() {
        return ringtonePlayer.getRingtone();
    }

    public void setRingtone(Uri ringtone) {
        ringtonePlayer.setRingtone(ringtone);
    }

    public Boolean getLooping() {
        return ringtonePlayer.getLooping();
    }

    public void setLooping(Boolean looping) {
        ringtonePlayer.setLooping(looping);
    }

    public Integer getVolume() {
        return ringtonePlayer.getVolume();
    }

    public void setVolume(Integer volume) {
        ringtonePlayer.setVolume(volume);
    }

    public Integer getFadeInTime() {
        return ringtonePlayer.getFadeInTime();
    }

    public void setFadeInTime(Integer fadeInTime) {
        ringtonePlayer.setFadeInTime(fadeInTime);
        vibratePlayer.setFadeInTime(fadeInTime);
    }

    public String getTtsText() {
        return ttsPlayer.getTtsText();
    }

    public void setTtsText(String ttsText) {
        ttsPlayer.setTtsText(ttsText);
    }

    public void addListener(RegularAlarmPlayerListener listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(RegularAlarmPlayerListener listener) {
        this.listenerList.remove(listener);
    }

    protected void fireStartEvent() {
        for (RegularAlarmPlayerListener listener : new ArrayList<RegularAlarmPlayerListener>(listenerList)) {
            listener.onStart();
        }
    }

    protected void fireStopEvent() {
        for (RegularAlarmPlayerListener listener : new ArrayList<RegularAlarmPlayerListener>(listenerList)) {
            listener.onStop();
        }
    }

    public synchronized void play() {
        if (playing) {
            stop();
        }
        playing = true;

        if (shouldRing()) {
            ringtonePlayer.addListener(ringtonePlayerListener);
            ringtonePlayer.play();
        }
        if (shouldSpeakAlarmName()) {
            ttsPlayer.addListener(ttsPlayerListener);
            ttsPlayer.play();
        }
        if (shouldVibrate()) {
            vibratePlayer.play();
        }

        timeoutHandler.sendMessageDelayed(timeoutHandler.obtainMessage(MSG_TIMEOUT), getPlayTimeMs());

        fireStartEvent();
    }

    public synchronized boolean isPlaying() {
        return playing;
    }

    public synchronized void stop() {
        if (!playing) {
            return;
        }
        playing = false;

        timeoutHandler.removeMessages(MSG_TIMEOUT);

        ringtonePlayer.removeListener(ringtonePlayerListener);
        ringtonePlayer.stop();

        ttsPlayer.removeListener(ttsPlayerListener);
        ttsPlayer.stop();

        vibratePlayer.stop();

        fireStopEvent();
    }

    private boolean shouldRing() {
        if (ring != null) {
            return ring;
        } else {
            return audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        }
    }

    private boolean shouldVibrate() {
        if (vibrate != null) {
            return vibrate;
        } else {
            return audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT;
        }
    }

    private boolean shouldSpeakAlarmName() {
        if (speakAlarmName != null) {
            return speakAlarmName;
        }

        return RACContext.isSpeakAlarmName() && shouldRing();
    }

    private Long getPlayTimeMs() {
        if (playTime != null) {
            return playTime * 1000l;
        }
        return RACContext.getDefaultAlarmTime() * 1000l;
    }

    /** 闹铃播放器监听器 */
    public static interface RegularAlarmPlayerListener {

        /**
         * 开始播放闹铃。
         */
        void onStart();

        /**
         * 结束播放闹铃。
         */
        void onStop();

    }

}
