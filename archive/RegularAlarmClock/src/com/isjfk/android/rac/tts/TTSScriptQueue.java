/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.tts;

import java.util.LinkedList;

import android.speech.tts.TextToSpeech;

/**
 * Tts脚本队列。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-9
 */
public class TTSScriptQueue extends LinkedList<TTSScript> {

    private static final long serialVersionUID = -2793499204614827774L;

    public boolean speakFinished(String utteranceId) {
        TTSScript script = peek();
        if ((script != null) && script.testUtteranceId(utteranceId)) {
            poll();
            return true;
        }
        return false;
    }

    public boolean hasNext() {
        return !isEmpty();
    }

    public int speakNext(TextToSpeech tts) {
        return peek().speak(tts);
    }

}
