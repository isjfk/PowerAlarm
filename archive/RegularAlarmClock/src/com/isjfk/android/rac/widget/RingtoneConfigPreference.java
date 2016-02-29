/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isjfk.android.rac.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.preference.RingtonePreference;
import android.util.AttributeSet;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.activity.RingtonePickerActivity;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 铃声首选项。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-4-4
 */
public class RingtoneConfigPreference extends RingtonePreference {

    public RingtoneConfigPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RingtoneConfigPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.ringtonePreferenceStyle);
    }

    public RingtoneConfigPreference(Context context) {
        this(context, null);
    }

    public RingtoneConfig getRingtoneConfig() {
        RingtoneConfig ringtoneConfig = RingtoneConfig.decode(getPersistedString(null));
        if (ringtoneConfig == null) {
            ringtoneConfig = new RingtoneConfig();
        }

        return ringtoneConfig;
    }

    public void saveRingtoneConfig(RingtoneConfig ringtoneConfig) {
        String str = RingtoneConfig.encode(ringtoneConfig);
        persistString(JavaUtil.isNotEmpty(str) ? str : "");
    }

    public void updateSummary(RingtoneConfig config) {
        if ((config == null) || (config.isDefault())) {
            setSummary(R.string.pref_ringtoneDefaultDesc);
        } else {
            Ringtone ringtone = RACUtil.getRingtone(getContext(), config.getRingtone());
            if (ringtone != null) {
                setSummary(ringtone.getTitle(getContext()));
            } else {
                setSummary(R.string.pref_ringtoneDefaultDesc);
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValueObj) {
        if (restorePersistedValue) {
            return;
        }

        String defaultValue = (String) defaultValueObj;
        if (!JavaUtil.isEmpty(defaultValue)) {
            saveRingtoneConfig(RingtoneConfig.decode(defaultValue));
        }
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        updateSummary(getRingtoneConfig());
    }

    @Override
    protected void onPrepareRingtonePickerIntent(Intent intent) {
        intent.setAction(null);
        intent.setClass(getContext(), RingtonePickerActivity.class);
        AndroidUtil.putExtra(intent, RingtonePickerActivity.SHOW_PREFERENCE_KEY, false);
        AndroidUtil.putExtra(intent, RingtonePickerActivity.RINGTONE_CONFIG_KEY, getRingtoneConfig());
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        RingtoneConfig config = null;
        if (data != null) {
            config = AndroidUtil.getExtra(data, RingtonePickerActivity.RINGTONE_CONFIG_KEY);
        }

        if ((resultCode == Activity.RESULT_OK) && (config != null)) {
            if (callChangeListener(RingtoneConfig.encode(config))) {
                saveRingtoneConfig(config);
                updateSummary(config);
            }
            return true;
        }

        return false;
    }

}
