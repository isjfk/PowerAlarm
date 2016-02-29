/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.tts;

import android.speech.tts.TextToSpeech;

/**
 * TTS语句脚本。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-9
 */
public class TTSSentenceScript extends TTSScript {

    private String sentence;

    public TTSSentenceScript(int streamType, String sentence) {
        super(streamType);
        this.sentence = sentence;
    }

    @Override
    public int speak(TextToSpeech tts) {
        return tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, genParams());
    }

}
