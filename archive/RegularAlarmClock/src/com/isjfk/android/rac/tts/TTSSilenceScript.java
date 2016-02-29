/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.tts;

import android.speech.tts.TextToSpeech;

/**
 * TTS静音脚本。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-9
 */
public class TTSSilenceScript extends TTSScript {

    private long duration;

    public TTSSilenceScript(int streamType, long duration) {
        super(streamType);
        this.duration = duration;
    }

    @Override
    public int speak(TextToSpeech tts) {
        return tts.playSilence(duration, TextToSpeech.QUEUE_FLUSH, genParams());
    }

}
