/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.tts;

import java.util.HashMap;

import android.speech.tts.TextToSpeech;

import com.isjfk.android.util.JavaUtil;

/**
 * TTS脚本。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-9
 */
public abstract class TTSScript {

    private static final String UTTERANCE_ID = "0001";

    private String streamType;

    public TTSScript(int streamType) {
        this.streamType = String.valueOf(streamType);
    }

    public abstract int speak(TextToSpeech tts);

    public boolean testUtteranceId(String utteranceId) {
        return JavaUtil.equals(getUtteranceId(), utteranceId);
    }

    protected HashMap<String, String> genParams() {
        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, streamType);
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, getUtteranceId());
        return ttsParams;
    }

    protected String getUtteranceId() {
        return UTTERANCE_ID;
    }

}
