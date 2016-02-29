/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.ringtone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Message;
import android.os.PowerManager;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 铃声播放器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-5
 */
public class RingtonePlayer {

    private static final int MSG_FADE_IN = 1;
    private static final int FADE_IN_INTERVAL = 200;

    private Context context;
    private AudioManager audioManager;
    private MediaPlayer player;

    private Integer streamType;
    private Integer streamVolume;

    private Uri ringtone = null;
    private Boolean looping = null;
    private Integer maxVolume;
    private Integer volume = null;
    private Integer fadeInTime = null;

    private List<RingtonePlayerListener> listenerList = new ArrayList<RingtonePlayerListener>();

    private boolean playing = false;
    private long startTime;
    private FadeInHandler fadeInHandler;

    public RingtonePlayer(Context context, AudioManager audioManager) {
        this.context = context;
        this.audioManager = audioManager;

        streamType = RACContext.getStreamType();
        maxVolume = RACContext.getMaxVolume();
    }

    public synchronized boolean isPlaying() {
        return playing;
    }

    public Boolean getLooping() {
        return looping;
    }

    public void setLooping(Boolean looping) {
        this.looping = looping;
    }

    public Uri getRingtone() {
        return ringtone;
    }

    public void setRingtone(Uri ringtone) {
        this.ringtone = ringtone;
    }

    public Integer getVolume() {
        return volume;
    }

    public synchronized void setVolume(Integer volume) {
        this.volume = volume;

        if (isPlaying()) {
            configVolumeForPlayer();
        }
    }

    public Integer getMaxVolume() {
        return maxVolume;
    }

    public synchronized void setMaxVolume(Integer maxVolume) {
        if (maxVolume == null) {
            this.maxVolume = RACContext.getMaxVolume();
        } else if (maxVolume > RACContext.getMaxVolume()) {
            this.maxVolume = RACContext.getMaxVolume();
        } else {
            this.maxVolume = maxVolume;
        }

        if (isPlaying()) {
            configVolumeForPlayer();
        }
    }

    public Integer getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(Integer fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public void addListener(RingtonePlayerListener listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(RingtonePlayerListener listener) {
        this.listenerList.remove(listener);
    }

    protected void fireStartEvent() {
        for (RingtonePlayerListener listener : new ArrayList<RingtonePlayerListener>(listenerList)) {
            listener.onStart();
        }
    }

    protected void fireStopEvent() {
        for (RingtonePlayerListener listener : new ArrayList<RingtonePlayerListener>(listenerList)) {
            listener.onStop();
        }
    }

    public void play() {
        if (playing) {
            stop();
        }
        playing = true;

        startTime = System.currentTimeMillis();

        Uri ringtone = getRingtone();
        try {
            if (ringtone == null) {
                ringtone = RACContext.getGlobalRingtoneConfig().getRingtone();
            }

            initPlayer();
            player.setDataSource(context, ringtone);
            doPlay();
        } catch (Exception e) {
            Log.e("error play alarm ringtone: " + ringtone, e);

            stop();
            playDefaultRingtone();
        }

        if ((fadeInTime != null) && (fadeInTime > 0)) {
            fadeInHandler = new FadeInHandler(this);
            fadeInHandler.sendMessageDelayed(fadeInHandler.obtainMessage(MSG_FADE_IN), FADE_IN_INTERVAL);
        }

        fireStartEvent();
    }

    public void stop() {
        if (!playing) {
            return;
        }
        playing = false;

        if ((streamType != null) && (streamVolume != null)) {
            audioManager.setStreamVolume(streamType, streamVolume, 0);
        }

        if (fadeInHandler != null) {
            fadeInHandler.removeMessages(MSG_FADE_IN);
            fadeInHandler = null;
        }

        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }

        startTime = 0;

        fireStopEvent();
    }

    private void playDefaultRingtone() {
        try {
            initPlayer();
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.default_ringtone);
            if (afd != null) {
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }
            doPlay();
        } catch (Exception e) {
            Log.e("error play default alarm ringtone", e);
        }
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("error play alarm ringtone, what: " + what + ", extra: " + extra);
                stop();
                return true;
            }
        });
        player.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
    }

    private void doPlay() throws Exception {
        streamVolume = audioManager.getStreamVolume(streamType);
        Integer streamMaxVolume = audioManager.getStreamMaxVolume(streamType);
        audioManager.setStreamVolume(streamType, streamMaxVolume, 0);

        player.setAudioStreamType(streamType);
        player.setLooping(shouldLooping());
        player.prepare();
        configVolumeForPlayer();
        player.start();
    }

    private void configVolumeForPlayer() {
        int playVolume = (volume != null) ? volume : RACContext.getGlobalRingtoneConfig().getVolume();
        double currentVolume = playVolume;

        if ((fadeInTime != null) && (fadeInTime > 0)) {
            long elapseTimeMill = System.currentTimeMillis() - startTime;
            long fadeInTimeMill = fadeInTime * 1000;

            if (elapseTimeMill < fadeInTimeMill) {
                currentVolume = (elapseTimeMill / (double) fadeInTimeMill) * playVolume;
            }
        }

        if (currentVolume < 0) {
            currentVolume = 0;
        } else if (currentVolume > maxVolume) {
            currentVolume = maxVolume;
        }

        float actualVolume = (float) (1 - (Math.log(maxVolume - currentVolume) / Math.log(maxVolume)));
        player.setVolume(actualVolume, actualVolume);
    }

    private boolean shouldLooping() {
        return (looping == null) ? true : looping;
    }

    private static class FadeInHandler extends WeakReferenceHandler<RingtonePlayer> {
        public FadeInHandler(RingtonePlayer player) {
            super(player);
        }

        /**
         * {@inheritDoc}
         * @see com.isjfk.android.util.WeakReferenceHandler#onMessage(java.lang.Object, android.os.Message)
         */
        @Override
        public void onMessage(RingtonePlayer player, Message msg) {
            switch (msg.what) {
            case MSG_FADE_IN:
                player.configVolumeForPlayer();

                Integer fadeInTime = player.fadeInTime;
                if ((fadeInTime != null) && (fadeInTime > 0)) {
                    long elapseTimeMill = System.currentTimeMillis() - player.startTime;
                    long fadeInTimeMill = fadeInTime * 1000;

                    if (elapseTimeMill < fadeInTimeMill) {
                        sendMessageDelayed(obtainMessage(MSG_FADE_IN), FADE_IN_INTERVAL);
                    }
                }
                break;
            }
        }
    };

    /** 铃声播放器监听器 */
    public static interface RingtonePlayerListener {

        /**
         * 开始播放铃声。
         */
        void onStart();

        /**
         * 结束播放铃声。
         */
        void onStop();

    }

}
