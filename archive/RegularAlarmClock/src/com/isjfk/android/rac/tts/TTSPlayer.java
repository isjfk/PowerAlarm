/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.tts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.util.JavaUtil;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 闹铃播放器。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-5
 */
public class TTSPlayer {

    private static final int MSG_TTS_SPEAK = 1;
    private static final int TTS_INIT_INTERVAL_TIME = 5 * 1000;
    private static final int TTS_INTERVAL_TIME = 10 * 1000;
    private static final int TTS_DELAY_TIME1 = 200;
    private static final int TTS_DELAY_TIME2 = 800;

    private Context context;

    private Integer ttsStreamType;

    private TextToSpeech tts = null;
    private TTSScriptQueue ttsQueue = null;
    private boolean ttsOK = false;
    private String ttsText = null;

    private List<TTSPlayerListener> listenerList = new ArrayList<TTSPlayerListener>();

    private boolean playing = false;

    public TTSPlayer(Context context) {
        super();
        this.context = context;
        this.ttsStreamType = RACContext.getTtsStreamType();
    }

    public String getTtsText() {
        return ttsText;
    }

    public void setTtsText(String ttsText) {
        if (JavaUtil.isAllAscii(ttsText)) {
            this.ttsText = ttsText;
        }
    }

    public synchronized boolean isPlaying() {
        return playing;
    }

    public void addListener(TTSPlayerListener listener) {
        this.listenerList.add(listener);
    }

    public void removeListener(TTSPlayerListener listener) {
        this.listenerList.remove(listener);
    }

    protected void fireStartSpeakEvent() {
        for (TTSPlayerListener listener : new ArrayList<TTSPlayerListener>(listenerList)) {
            listener.onStartSpeak();
        }
    }

    protected void fireEndSpeakEvent() {
        for (TTSPlayerListener listener : new ArrayList<TTSPlayerListener>(listenerList)) {
            listener.onEndSpeak();
        }
    }

    public synchronized void play() {
        if (playing) {
            stop();
        }

        if (RACContext.isSpeakAlarmName() && JavaUtil.isNotEmpty(ttsText)) {
            try {
                tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        ttsOK = (status == TextToSpeech.SUCCESS);

                        if (ttsOK && (tts != null)) {
                            tts.setLanguage(Locale.US);
                            tts.setOnUtteranceCompletedListener(utteranceListener);
                            scheduleNextSpeak(TTS_INIT_INTERVAL_TIME);
                        }
                    }
                });

                playing = true;
            } catch (Exception e) {
                Log.e("error open TextToSpeech service", e);
                if (tts != null) {
                    try {
                        tts.shutdown();
                    } catch (Exception e2) {
                        Log.e("error shutdown TextToSpeech service", e2);
                    } finally {
                        tts = null;
                    }
                }
            }
        }
    }

    public synchronized void stop() {
        if (!playing) {
            return;
        }

        ttsHandler.removeMessages(MSG_TTS_SPEAK);
        if (tts != null) {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            tts.shutdown();
            tts = null;
        }

        playing = false;
    }

    private boolean shouldSpeak() {
        return (tts != null) && ttsOK;
    }

    private synchronized void speak() {
        if (shouldSpeak()) {
            ttsQueue = new TTSScriptQueue();
            ttsQueue.offer(new TTSSilenceScript(ttsStreamType, TTS_DELAY_TIME1));
            ttsQueue.offer(new TTSSentenceScript(ttsStreamType, ttsText));
            ttsQueue.offer(new TTSSilenceScript(ttsStreamType, TTS_DELAY_TIME2));
            ttsQueue.offer(new TTSSentenceScript(ttsStreamType, ttsText));
            ttsQueue.offer(new TTSSilenceScript(ttsStreamType, TTS_DELAY_TIME1));

            fireStartSpeakEvent();

            ttsQueue.speakNext(tts);
        }
    }

    OnUtteranceCompletedListener utteranceListener = new OnUtteranceCompletedListener() {
        @Override
        public void onUtteranceCompleted(String utteranceId) {
            if (ttsQueue.speakFinished(utteranceId)) {
                if (ttsQueue.hasNext()) {
                    ttsQueue.speakNext(tts);
                } else {
                    finishSpeak();
                }
            }
        }
    };

    private synchronized void finishSpeak() {
        if (tts != null) {
            scheduleNextSpeak(TTS_INTERVAL_TIME);
        }

        fireEndSpeakEvent();
    }

    private Handler ttsHandler = new TTSHandler(this);

    private synchronized void scheduleNextSpeak(long delayTime) {
        if (shouldSpeak()) {
            ttsHandler.sendMessageDelayed(ttsHandler.obtainMessage(MSG_TTS_SPEAK), delayTime);
        }
    }

    private static class TTSHandler extends WeakReferenceHandler<TTSPlayer> {
        public TTSHandler(TTSPlayer player) {
            super(player);
        }

        /**
         * {@inheritDoc}
         * @see com.isjfk.android.util.WeakReferenceHandler#onMessage(java.lang.Object, android.os.Message)
         */
        @Override
        public void onMessage(TTSPlayer player, Message msg) {
            switch (msg.what) {
            case MSG_TTS_SPEAK:
                player.speak();
                break;
            }
        }
    };

    /** TTS播放器监听器 */
    public static interface TTSPlayerListener {

        /**
         * 开始播放TTS语音。
         */
        void onStartSpeak();

        /**
         * 结束播放TTS语音。
         */
        void onEndSpeak();

    }

}
