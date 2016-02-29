/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.speech.tts.TextToSpeech;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACContext.ConfigEnum;
import com.isjfk.android.rac.common.RACContext.PrefAlarmTimeEnum;
import com.isjfk.android.rac.common.RACContext.PrefRingEnum;
import com.isjfk.android.rac.widget.RingtoneConfigPreference;
import com.isjfk.android.util.JavaUtil;

/**
 * 规则闹钟参数界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-14
 */
public class RACPreferenceActivity extends PreferenceActivity {

    private RingtoneConfigPreference ringtonePref;
    private CheckBoxPreference speakAlarmNamePref;
    private ListPreference alarmTimePref;

    private boolean needUpdateStatusBar = false;

    private Handler handler = new Handler();
    private TextToSpeech tts = null;

    /**
     * {@inheritDoc}
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        needUpdateStatusBar = false;

        ringtonePref = (RingtoneConfigPreference) findPreference(ConfigEnum.Ringtone);

        ListPreference ring = (ListPreference) findPreference(ConfigEnum.Ring);
        ring.setSummary(ring.getEntry());
        ring.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference ring = (ListPreference) preference;
                    ring.setSummary(ring.getEntries()[ring.findIndexOfValue((String) newValue)]);
                    updateRingRelatedWidget(JavaUtil.toInteger((String) newValue));
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.Ring + " with new value " + newValue, e);
                }
                return true;
            }
        });

        speakAlarmNamePref = (CheckBoxPreference) findPreference(ConfigEnum.SpeakAlarmName);
        speakAlarmNamePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    if ((Boolean) newValue) {
                        checkTts();
                    }
                }
                return true;
            }
        });

        ListPreference vibrate = (ListPreference) findPreference(ConfigEnum.Vibrate);
        vibrate.setSummary(vibrate.getEntry());
        vibrate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference vibrate = (ListPreference) preference;
                    vibrate.setSummary(vibrate.getEntries()[vibrate.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.Vibrate + " with new value " + newValue, e);
                }
                return true;
            }
        });

        alarmTimePref = (ListPreference) findPreference(ConfigEnum.AlarmTime);
        alarmTimePref.setSummary(alarmTimePref.getEntry());
        alarmTimePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference alarmTime = (ListPreference) preference;
                    alarmTime.setSummary(alarmTime.getEntries()[alarmTime.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.AlarmTime + " with new value " + newValue, e);
                }
                return true;
            }
        });

        ListPreference snoozeTime = (ListPreference) findPreference(ConfigEnum.SnoozeTime);
        snoozeTime.setSummary(snoozeTime.getEntry());
        snoozeTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference snoozeTime = (ListPreference) preference;
                    snoozeTime.setSummary(snoozeTime.getEntries()[snoozeTime.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.SnoozeTime + " with new value " + newValue, e);
                }
                return true;
            }
        });

        ListPreference autoSnoozeTimes = (ListPreference) findPreference(ConfigEnum.AutoSnoozeTimes);
        autoSnoozeTimes.setSummary(autoSnoozeTimes.getEntry());
        autoSnoozeTimes.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference autoSnoozeTimes = (ListPreference) preference;
                    autoSnoozeTimes.setSummary(autoSnoozeTimes.getEntries()[autoSnoozeTimes.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.AutoSnoozeTimes + " with new value " + newValue, e);
                }
                return true;
            }
        });

        CheckBoxPreference alarmTimeNotification = (CheckBoxPreference) findPreference(ConfigEnum.AlarmTimeNotification);
        alarmTimeNotification.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                needUpdateStatusBar = true;
                return true;
            }
        });

        CheckBoxPreference time24HourFormat = (CheckBoxPreference) findPreference(ConfigEnum.Time24HourFormat);
        time24HourFormat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                needUpdateStatusBar = true;
                return true;
            }
        });

        ListPreference dateFormat = (ListPreference) findPreference(ConfigEnum.DateFormat);
        dateFormat.setSummary(dateFormat.getEntry());
        dateFormat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference dateFormat = (ListPreference) preference;
                    dateFormat.setSummary(dateFormat.getEntries()[dateFormat.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.DateFormat + " with new value " + newValue, e);
                }
                needUpdateStatusBar = true;
                return true;
            }
        });

        ListPreference streamType = (ListPreference) findPreference(ConfigEnum.StreamType);
        streamType.setSummary(streamType.getEntry());
        streamType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference streamType = (ListPreference) preference;
                    streamType.setSummary(streamType.getEntries()[streamType.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.StreamType + " with new value " + newValue, e);
                }
                return true;
            }
        });

        ListPreference ttsStreamType = (ListPreference) findPreference(ConfigEnum.TTSStreamType);
        ttsStreamType.setSummary(ttsStreamType.getEntry());
        ttsStreamType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    ListPreference streamType = (ListPreference) preference;
                    streamType.setSummary(streamType.getEntries()[streamType.findIndexOfValue((String) newValue)]);
                } catch (Exception e) {
                    Log.e("error process preference " + ConfigEnum.TTSStreamType + " with new value " + newValue, e);
                }
                return true;
            }
        });

        updateRingRelatedWidget(JavaUtil.toInteger(ring.getValue()));
    }

    /**
     * {@inheritDoc}
     * @see android.preference.PreferenceActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        shutdownTts();

        RACContext.init(this);
        if (needUpdateStatusBar) {
            // 重设闹铃以刷新状态栏通知
            RACContext.resetAlarm(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RACContext.init(this);
    }

    private void updateRingRelatedWidget(Integer ring) {
        if (PrefRingEnum.NORING.equals(ring)) {
            ringtonePref.setEnabled(false);

            Integer orgAlarmTime = JavaUtil.toInteger(alarmTimePref.getValue());
            alarmTimePref.setEntries(R.array.pref_alarmTimeNoRingtoneEntries);
            alarmTimePref.setEntryValues(R.array.pref_alarmTimeNoRingtoneEntryValues);
            if (PrefAlarmTimeEnum.RINGTONE_LENGTH.equals(orgAlarmTime)) {
                alarmTimePref.setValue(String.valueOf(RACContext.getDefaultAlarmTime()));
            }
        } else {
            ringtonePref.setEnabled(true);

            alarmTimePref.setEntries(R.array.pref_alarmTimeEntries);
            alarmTimePref.setEntryValues(R.array.pref_alarmTimeEntryValues);
        }
    }

    private void checkTts() {
        try {
            shutdownTts();
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int paramInt) {
                    if (TextToSpeech.LANG_MISSING_DATA == tts.isLanguageAvailable(Locale.US)) {
                        onTtsInstallDataRequired();
                    } else {
                        onTtsSupported();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            shutdownTts();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.e("error open TextToSpeech service", e);
            onTtsNotSupported();
        }
    }

    private void onTtsSupported() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RACPreferenceActivity.this);
        builder.setMessage(R.string.pref_speakAlarmNameTips);
        builder.setNeutralButton(R.string.buttonOK, null);
        builder.show();
    }

    private void onTtsNotSupported() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                speakAlarmNamePref.setChecked(false);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(RACPreferenceActivity.this);
        builder.setMessage(R.string.pref_speakAlarmNameNotSupportTips);
        builder.setNeutralButton(R.string.buttonOK, null);
        builder.show();
    }

    private void onTtsInstallDataRequired() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                speakAlarmNamePref.setChecked(false);
            }
        });

        try {
            startActivity(new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA));
        } catch (Exception e) {
            Log.e("error open TextToSpeech install data activity", e);
            onTtsNotSupported();
        }
    }

    private void shutdownTts() {
        if (tts != null) {
            try {
                tts.shutdown();
            } catch (Exception e) {
                Log.e("error shutdown TextToSpeech service", e);
            }
            tts = null;
        }
    }

}
