/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmPlayer;
import com.isjfk.android.rac.RegularAlarmPlayer.RegularAlarmPlayerListener;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.util.AndroidUtil;

/**
 * 铃声选择界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-10-30
 */
public class RingtonePickerActivity extends Activity {

    public static final String RINGTONE_CONFIG_KEY = "config";
    public static final String SHOW_PREFERENCE_KEY = "showPreference";
    public static final String SHOW_DEFAULT_KEY = "showDefault";
    public static final String SHOW_VOLUME_KEY = "showVolume";

    private static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";

    private static final int REQ_RINGTONE = 1;
    private static final int REQ_MUSIC = 2;

    private RegularAlarmPlayer player = null;

    private RingtoneConfig config = null;
    private boolean showPreference = true;
    private boolean showDefault = true;

    private LinearLayout typePreferenceGroup;
    private RadioButton typePreferenceButton;
    private LinearLayout typeDefaultGroup;
    private RadioButton typeDefaultButton;
    private LinearLayout typeRingtoneGroup;
    private RadioButton typeRingtoneButton;
    private LinearLayout typeMusicGroup;
    private RadioButton typeMusicButton;

    private TextView typeRingtoneDesc;
    private TextView typeMusicDesc;

    private LinearLayout volumeGroup;
    private SeekBar volumeSeekBar;

    private LinearLayout fadeInTimeGroup;
    private Spinner fadeInTimeSpinner;

    private ToggleButton previewButton;

    private Button okButton;
    private Button cancelButton;

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtone_picker);

        Intent intent = getIntent();

        showPreference = AndroidUtil.getExtra(intent, SHOW_PREFERENCE_KEY, showPreference);
        showDefault = AndroidUtil.getExtra(intent, SHOW_DEFAULT_KEY, showDefault);

        config = AndroidUtil.getExtra(intent, RINGTONE_CONFIG_KEY);
        if (config == null) {
            config = new RingtoneConfig();
            if (showPreference) {
                config.setType(RingtoneConfig.TYPE_PREFERENCE);
            } else {
                config.setType(RingtoneConfig.TYPE_DEFAULT);
            }
        }

        player = new RegularAlarmPlayer(this);
        player.setRing(true);
        player.setLooping(false);
        player.setVibrate(false);
        player.addListener(new RegularAlarmPlayerListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onStop() {
                stopPreview();
            }
        });

        typePreferenceGroup = (LinearLayout) findViewById(R.id.ringtoneTypePreferenceGroup);
        typePreferenceButton = (RadioButton) findViewById(R.id.ringtoneTypePreferenceButton);
        typeDefaultGroup = (LinearLayout) findViewById(R.id.ringtoneTypeDefaultGroup);
        typeDefaultButton = (RadioButton) findViewById(R.id.ringtoneTypeDefaultButton);
        typeRingtoneGroup = (LinearLayout) findViewById(R.id.ringtoneTypeRingtoneGroup);
        typeRingtoneButton = (RadioButton) findViewById(R.id.ringtoneTypeRingtoneButton);
        typeMusicGroup = (LinearLayout) findViewById(R.id.ringtoneTypeMusicGroup);
        typeMusicButton = (RadioButton) findViewById(R.id.ringtoneTypeMusicButton);

        typeRingtoneDesc = (TextView) findViewById(R.id.ringtoneTypeRingtoneDesc);
        typeMusicDesc = (TextView) findViewById(R.id.ringtoneTypeMusicDesc);

        volumeGroup = (LinearLayout) findViewById(R.id.ringtoneVolumeGroup);
        volumeSeekBar = (SeekBar) findViewById(R.id.ringtoneVolumeBar);

        fadeInTimeGroup = (LinearLayout) findViewById(R.id.ringtoneFadeInTimeGroup);
        fadeInTimeSpinner = (Spinner) findViewById(R.id.ringtoneFadeInTime);

        previewButton = (ToggleButton) findViewById(R.id.ringtonePreview);

        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        fixConfig();
        setupView();
        refreshView();
    }

    private void fixConfig() {
        if (!RACUtil.isRingtoneValid(this, config.getRingtone(RingtoneConfig.TYPE_RINGTONE))) {
            config.getRingtones().remove(RingtoneConfig.TYPE_RINGTONE);
            if (config.isRingtone()) {
                config.setType(showPreference ? RingtoneConfig.TYPE_PREFERENCE : RingtoneConfig.TYPE_DEFAULT);
            }
        }

        if (!RACUtil.isRingtoneValid(this, config.getRingtone(RingtoneConfig.TYPE_MUSIC))) {
            config.getRingtones().remove(RingtoneConfig.TYPE_MUSIC);
            if (config.isMusic()) {
                config.setType(showPreference ? RingtoneConfig.TYPE_PREFERENCE : RingtoneConfig.TYPE_DEFAULT);
            }
        }

        if (config.getVolume() < 0) {
            config.setVolume(0);
        } else if (config.getVolume() > RACContext.getMaxVolume()) {
            config.setVolume(RACContext.getMaxVolume());
        }

        if (toFadeInTimeIndex(config.getFadeInTime()) == -1) {
            config.setFadeInTime(RACContext.getDefaultFadeInTime());
        }
    }

    private void setupView() {
        if (showPreference) {
            typePreferenceGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    config.setType(RingtoneConfig.TYPE_PREFERENCE);
                    refreshView();

                    playPreview();
                }
            });
        } else {
            typePreferenceGroup.setVisibility(View.GONE);
        }

        if (showDefault) {
            typeDefaultGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    config.setType(RingtoneConfig.TYPE_DEFAULT);
                    refreshView();

                    playPreview();
                }
            });
        } else {
            typeDefaultGroup.setVisibility(View.GONE);
        }

        typeRingtoneGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPreview();

                if (!config.isRingtone() && (config.getRingtone(RingtoneConfig.TYPE_RINGTONE) != null)) {
                    config.setType(RingtoneConfig.TYPE_RINGTONE);
                    refreshView();

                    playPreview();
                } else {
                    try {
                        selectRingtone();
                    } catch (Exception e) {
                        Log.e("error open Android Ringtone Picker Activity", e);
                        try {
                            selectRingtoneForSamsung();
                        } catch (Exception e2) {
                            Log.e("error open Android Ringtone Picker Activity for Samsung", e2);
                            RACUtil.popupError(RingtonePickerActivity.this, R.string.errOpenRingtonePicker);
                        }
                    }
                }
            }
        });

        typeMusicGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPreview();

                if (!config.isMusic() && (config.getRingtone(RingtoneConfig.TYPE_MUSIC) != null)) {
                    config.setType(RingtoneConfig.TYPE_MUSIC);
                    refreshView();

                    playPreview();
                } else {
                    try {
                        selectMusic();
                    } catch (Exception e) {
                        RACUtil.popupError(RingtonePickerActivity.this, R.string.errOpenMusicPicker);
                    }
                }
            }
        });

        volumeSeekBar.setMax(RACContext.getMaxVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isPreviewing()) {
                    player.setVolume(progress);
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                config.setVolume(seekBar.getProgress());
                if (isPreviewing()) {
                    player.setVolume(config.getVolume());
                } else {
                    playPreview();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });

        fadeInTimeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int orgFadeInTime = config.getFadeInTime();

                config.setFadeInTime(toFadeInTime(position));

                if (orgFadeInTime != config.getFadeInTime()) {
                    playPreview();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fadeInTimeSpinner.setSelection(toFadeInTimeIndex(RACContext.getDefaultFadeInTime()));
            }
        });

        previewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPreviewing()) {
                    stopPreview();
                } else {
                    playPreview();
                }
            }
        });

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                AndroidUtil.putExtra(intent, RINGTONE_CONFIG_KEY, config);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                AndroidUtil.putExtra(intent, RINGTONE_CONFIG_KEY, config);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private void refreshView() {
        if (config.isRingtone()) {
            typePreferenceButton.setChecked(false);
            typeDefaultButton.setChecked(false);
            typeRingtoneButton.setChecked(true);
            typeMusicButton.setChecked(false);
            volumeGroup.setEnabled(true);
            volumeSeekBar.setEnabled(true);
            fadeInTimeGroup.setEnabled(true);
            fadeInTimeSpinner.setEnabled(true);
        } else if (config.isMusic()) {
            typePreferenceButton.setChecked(false);
            typeDefaultButton.setChecked(false);
            typeRingtoneButton.setChecked(false);
            typeMusicButton.setChecked(true);
            volumeGroup.setEnabled(true);
            volumeSeekBar.setEnabled(true);
            fadeInTimeGroup.setEnabled(true);
            fadeInTimeSpinner.setEnabled(true);
        } else if (config.isPreference()) {
            typePreferenceButton.setChecked(true);
            typeDefaultButton.setChecked(false);
            typeRingtoneButton.setChecked(false);
            typeMusicButton.setChecked(false);
            volumeGroup.setEnabled(false);
            volumeSeekBar.setEnabled(false);
            fadeInTimeGroup.setEnabled(false);
            fadeInTimeSpinner.setEnabled(false);
        } else {        // Default
            typePreferenceButton.setChecked(false);
            typeDefaultButton.setChecked(true);
            typeRingtoneButton.setChecked(false);
            typeMusicButton.setChecked(false);
            volumeGroup.setEnabled(true);
            volumeSeekBar.setEnabled(true);
            fadeInTimeGroup.setEnabled(true);
            fadeInTimeSpinner.setEnabled(true);
        }

        Ringtone ringtone = RACUtil.getRingtone(this, config.getRingtone(RingtoneConfig.TYPE_RINGTONE));
        if (ringtone != null) {
            typeRingtoneDesc.setText(ringtone.getTitle(this));
        } else {
            typeRingtoneDesc.setText(R.string.ringtoneTypeRingtoneDesc);
        }

        Ringtone music = RACUtil.getRingtone(this, config.getRingtone(RingtoneConfig.TYPE_MUSIC));
        if (music != null) {
            typeMusicDesc.setText(music.getTitle(this));
        } else {
            typeMusicDesc.setText(R.string.ringtoneTypeMusicDesc);
        }

        volumeSeekBar.setProgress(config.getVolume());

        fadeInTimeSpinner.setSelection(toFadeInTimeIndex(config.getFadeInTime()));
    }

    private void playPreview() {
        player.stop();

        player.configRingtone(config);
        player.play();

        previewButton.setChecked(true);
    }

    private void stopPreview() {
        player.stop();

        previewButton.setChecked(false);
    }

    private boolean isPreviewing() {
        return player.isPlaying();
    }

    private void selectRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);

        Uri ringtoneUri = config.getRingtone(RingtoneConfig.TYPE_RINGTONE);
        if (ringtoneUri != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
        }

        startActivityForResult(intent, REQ_RINGTONE);
    }

    /**
     * 处理三星手机内置的RingtonePickerActivity报空指针问题。
     */
    private void selectRingtoneForSamsung() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);      // this should work
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);

        Uri ringtoneUri = config.getRingtone(RingtoneConfig.TYPE_RINGTONE);
        if (ringtoneUri != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
        }

        startActivityForResult(intent, REQ_RINGTONE);
    }

    private void selectMusic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(EXTRA_LOCAL_ONLY, true);

        Uri musicUri = config.getRingtone(RingtoneConfig.TYPE_MUSIC);
        if (musicUri != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, musicUri);
        }

        startActivityForResult(intent, REQ_MUSIC);
    }

    private int toFadeInTime(int index) {
        return getFadeInTimeArray()[index];
    }

    private int toFadeInTimeIndex(int fadeInTime) {
        int[] fadeInTimeArray = getFadeInTimeArray();
        for (int i = 0; i < fadeInTimeArray.length; i++) {
            if (fadeInTimeArray[i] == fadeInTime) {
                return i;
            }
        }
        return -1;
    }

    private int[] getFadeInTimeArray() {
        String[] fadeInTimeValues = getResources().getStringArray(R.array.ringtoneFadeInTimeValues);
        int[] fadeInTimeArray = new int[fadeInTimeValues.length];
        for (int i = 0; i < fadeInTimeValues.length; i++) {
            fadeInTimeArray[i] = Integer.valueOf(fadeInTimeValues[i]);
        }
        return fadeInTimeArray;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        stopPreview();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RINGTONE) {
            if (data != null) {
                Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (RACUtil.isRingtoneValid(this, ringtoneUri)) {
                    config.setType(RingtoneConfig.TYPE_RINGTONE);
                    config.setRingtone(ringtoneUri);
                    refreshView();
                    playPreview();
                }
            }
        } else if (requestCode == REQ_MUSIC) {
            if (data != null) {
                Uri musicUri = data.getData();
                if (RACUtil.isRingtoneValid(this, musicUri)) {
                    config.setType(RingtoneConfig.TYPE_MUSIC);
                    config.setRingtone(musicUri);
                    refreshView();
                    playPreview();
                }
            }
        } else {
            Log.e("unknown request code: " + requestCode);
        }
    }

}
