/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmDataService.ResultCode;
import com.isjfk.android.rac.RegularAlarmDataServiceClient;
import com.isjfk.android.rac.SliderContainer;
import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.AlarmRule.Columns.AlarmTimeEnum;
import com.isjfk.android.rac.bean.AlarmRule.Columns.RingEnum;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.rac.callback.AlarmRuleCallback;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.common.RACUtil.SaveOnCloseAction;
import com.isjfk.android.rac.rule.RuleUtil;
import com.isjfk.android.rac.widget.DateRuleListEditor;
import com.isjfk.android.rac.widget.DateRuleListViewer;
import com.isjfk.android.rac.widget.DateRuleListViewer.DateRuleChangedListener;
import com.isjfk.android.rac.widget.EditTextDefault;
import com.isjfk.android.util.AndroidUtil;

/**
 * 日期规则详细信息界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-9-1
 */
public class AlarmRuleEditActivity extends Activity implements AlarmRuleCallback {

    public static final String MODE_KEY = "mode";
    public static final String ALARMRULE_KEY = "alarmRule";

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;

    private static final int REQ_RINGTONE_PICKER = 1;

    private int mode = MODE_ADD;
    private AlarmRule alarmRule = null;

    private EditTextDefault nameText;
    private TextView timeText;
    private SliderContainer timeSlider;
    private DateRuleListEditor dateRuleEditor;
    private LinearLayout delAfterExpiredGroup;
    private ToggleButton delAfterExpiredButton;
    private LinearLayout complyWorkdayGroup;
    private ToggleButton complyWorkdayButton;
    private Button ringtoneButton;
    private Spinner ringSpinner;
    private Spinner vibrateSpinner;
    private Spinner alarmTimeSpinner;
    private Spinner alarmTimeNoRingtoneSpinner;

    private RegularAlarmDataServiceClient client = new RegularAlarmDataServiceClient(this, this);

    private DateRuleChangedListener dateRuleChangedListener = new DateRuleChangedListener() {
        @Override
        public void onDateRuleChanged(DateRuleListViewer view, List<DateRule> dateRuleList) {
            if (RuleUtil.isExpireable(dateRuleList)) {
                if (delAfterExpiredGroup.getVisibility() == View.GONE) {
                    delAfterExpiredButton.setChecked(true);
                    delAfterExpiredGroup.setVisibility(View.VISIBLE);
                }
            } else {
                if (delAfterExpiredGroup.getVisibility() == View.VISIBLE) {
                    delAfterExpiredButton.setChecked(true);
                    delAfterExpiredGroup.setVisibility(View.GONE);
                }
            }

            if (RuleUtil.isOneTime(dateRuleList)) {
                if (complyWorkdayGroup.getVisibility() == View.VISIBLE) {
                    complyWorkdayGroup.setVisibility(View.GONE);
                }
            } else {
                if (complyWorkdayGroup.getVisibility() == View.GONE) {
                    complyWorkdayGroup.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmrule_edit);

        Button backButton = (Button) findViewById(R.id.titleBarBackButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Button saveButton = (Button) findViewById(R.id.titleBarSaveButton);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        Intent intent = getIntent();

        mode = AndroidUtil.getExtra(intent, MODE_KEY, MODE_ADD);
        alarmRule = AndroidUtil.getExtra(intent, ALARMRULE_KEY);
        if (mode == MODE_ADD) {
            ((TextView) findViewById(R.id.titleBarText)).setText(R.string.alarmRuleNewTitle);
            if (alarmRule == null) {
                alarmRule = BeanFactory.newAlarmRule();
            }
        } else {
            ((TextView) findViewById(R.id.titleBarText)).setText(R.string.alarmRuleEditTitle);
            if (alarmRule == null) {
                RACUtil.popupError(this, R.string.errAlarmRuleNoItemToEdit);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }

        Calendar alarmTime = alarmRule.getTimeAsCalendar();

        nameText = (EditTextDefault) findViewById(R.id.alarmRuleName);
        nameText.setText(alarmRule.getName());

        timeText = (TextView) findViewById(R.id.alarmRuleTimeText);
        timeText.setText(RACTimeUtil.formatTime(alarmTime));

        timeSlider = (SliderContainer) findViewById(R.id.alarmRuleTime);
        timeSlider.setMinuteInterval(1);
        timeSlider.setTime(alarmTime);

        delAfterExpiredGroup = (LinearLayout) findViewById(R.id.alarmRuleDelAfterExpiredGroup);
        delAfterExpiredButton = (ToggleButton) findViewById(R.id.alarmRuleDelAfterExpired);

        complyWorkdayGroup = (LinearLayout) findViewById(R.id.alarmRuleComplyWorkdayGroup);
        complyWorkdayButton = (ToggleButton) findViewById(R.id.alarmRuleComplyWorkday);

        // 必须放在获取delAfterExpiredGroup和complyWorkdayGroup之后，否则dateRuleChangedListener无法处理
        dateRuleEditor = (DateRuleListEditor) findViewById(R.id.dateRuleListEditor);
        dateRuleEditor.addDateRuleChangedListener(dateRuleChangedListener);
        dateRuleEditor.setDateRuleList(alarmRule.getDateRuleList());

        // 放在dateRuleEditor.setDateRuleLis()之后以正确显示被编辑的闹铃规则
        delAfterExpiredButton.setChecked(alarmRule.isDelAfterExpired());
        complyWorkdayButton.setChecked(alarmRule.isComplyWorkday());

        ringtoneButton = (Button) findViewById(R.id.alarmRuleRingtone);
        ringtoneButton.setText(RACUtil.getRingtoneName(this, alarmRule.getRingtone()));

        ringSpinner = (Spinner) findViewById(R.id.alarmRuleRing);
        ringSpinner.setSelection(alarmRule.getRing());

        vibrateSpinner = (Spinner) findViewById(R.id.alarmRuleVibrate);
        vibrateSpinner.setSelection(alarmRule.getVibrate());

        alarmTimeSpinner = (Spinner) findViewById(R.id.alarmRuleAlarmTime);
        alarmTimeNoRingtoneSpinner = (Spinner) findViewById(R.id.alarmRuleAlarmTimeNoRingtone);
        if (RingEnum.NORING.equals(alarmRule.getRing())) {
            alarmTimeNoRingtoneSpinner.setSelection(toAlarmTimeNoRingtoneIndex(alarmRule.getAlarmTime()));
            alarmTimeSpinner.setVisibility(View.GONE);
            alarmTimeNoRingtoneSpinner.setVisibility(View.VISIBLE);
        } else {
            alarmTimeSpinner.setSelection(toAlarmTimeIndex(alarmRule.getAlarmTime()));
            alarmTimeSpinner.setVisibility(View.VISIBLE);
            alarmTimeNoRingtoneSpinner.setVisibility(View.GONE);
        }

        ringSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (RingEnum.NORING.equals(position)) {
                    ringtoneButton.setEnabled(false);

                    if (alarmTimeSpinner.getVisibility() == View.VISIBLE) {
                        int alarmTime = toAlarmTime(alarmTimeSpinner.getSelectedItemPosition());
                        if (AlarmTimeEnum.RINGTONE_LENGTH.equals(alarmTime)) {
                            alarmTime = AlarmTimeEnum.DEFAULT;
                        }
                        alarmTimeNoRingtoneSpinner.setSelection(toAlarmTimeNoRingtoneIndex(alarmTime));
                    }

                    alarmTimeSpinner.setVisibility(View.GONE);
                    alarmTimeNoRingtoneSpinner.setVisibility(View.VISIBLE);
                } else {
                    ringtoneButton.setEnabled(true);

                    if (alarmTimeNoRingtoneSpinner.getVisibility() == View.VISIBLE) {
                        int alarmTime = toAlarmTimeNoRingtone(alarmTimeNoRingtoneSpinner.getSelectedItemPosition());
                        alarmTimeSpinner.setSelection(toAlarmTimeIndex(alarmTime));
                    }

                    alarmTimeSpinner.setVisibility(View.VISIBLE);
                    alarmTimeNoRingtoneSpinner.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ringtoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                Intent intent = new Intent(AlarmRuleEditActivity.this, RingtonePickerActivity.class);
                AndroidUtil.putExtra(intent, RingtonePickerActivity.RINGTONE_CONFIG_KEY, alarmRule.getRingtone());
                startActivityForResult(intent, REQ_RINGTONE_PICKER);
            }
        });

        timeSlider.setOnTimeChangeListener(new SliderContainer.OnTimeChangeListener() {
            @Override
            public void onTimeChange(Calendar time) {
                timeText.setText(RACTimeUtil.formatTime(time));
            }
        });
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        RACUtil.popupTips(this, R.string.ruleEditTip);
        client.bindService();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        client.unbindService();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        RACUtil.saveOnCloseConfirm(new SaveOnCloseAction(this) {
            @Override
            public void save() {
                saveChanges();
            }
            @Override
            public void close() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_options_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuHelp:
            RACUtil.openHelpActivity(this, "alarm-create");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQ_RINGTONE_PICKER) && (resultCode == RESULT_OK) && (data != null)) {
            RingtoneConfig ringtone = AndroidUtil.getExtra(data, RingtonePickerActivity.RINGTONE_CONFIG_KEY);
            if (ringtone != null) {
                alarmRule.setRingtone(ringtone);
                ringtoneButton.setText(RACUtil.getRingtoneName(this, ringtone));
            }
        }
    }

    /**
     * 保存修改并退出。
     */
    public void saveChanges() {
        Calendar alarmTime = timeSlider.getTime();

        alarmRule.setName(nameText.getTextOrDefault());
        alarmRule.setHour(alarmTime.get(Calendar.HOUR_OF_DAY));
        alarmRule.setMinute(alarmTime.get(Calendar.MINUTE));
        alarmRule.setDateRuleList(dateRuleEditor.getDateRuleList());
        if (RuleUtil.isExpireable(alarmRule.getDateRuleList())) {
            alarmRule.setDelAfterExpired(delAfterExpiredButton.isChecked());
        } else {
            alarmRule.setDelAfterExpired(false);
        }
        if (RuleUtil.isOneTime(alarmRule.getDateRuleList())) {
            alarmRule.setComplyWorkday(false);
        } else {
            alarmRule.setComplyWorkday(complyWorkdayButton.isChecked());
        }
        alarmRule.setRing(ringSpinner.getSelectedItemPosition());
        alarmRule.setVibrate(vibrateSpinner.getSelectedItemPosition());
        if (RingEnum.NORING.equals(alarmRule.getRing())) {
            alarmRule.setAlarmTime(toAlarmTimeNoRingtone(alarmTimeNoRingtoneSpinner.getSelectedItemPosition()));
        } else {
            alarmRule.setAlarmTime(toAlarmTime(alarmTimeSpinner.getSelectedItemPosition()));
        }
        alarmRule.setEnabled(true);
        alarmRule.setActived(true);

        if (mode == MODE_ADD) {
            client.addAlarmRule(alarmRule);
        } else {
            client.updateAlarmRule(alarmRule);
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.AlarmRuleCallback#onAddAlarmRule(android.content.Context, java.lang.Integer, com.isjfk.android.rac.bean.AlarmRule)
     */
    @Override
    public void onAddAlarmRule(Context context, Integer resultCode, AlarmRule alarmRule) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.alarmRuleAddSuccess);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            RACUtil.popupError(this, R.string.errAlarmRuleAddFailed);
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.AlarmRuleCallback#onUpdateAlarmRule(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onUpdateAlarmRule(Context context, Integer resultCode) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.alarmRuleUpdateSuccess);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            RACUtil.popupError(this, R.string.errAlarmRuleUpdateFailed);
        }
    }

    private int toAlarmTime(int index) {
        return getAlarmTimes()[index];
    }

    private int toAlarmTimeIndex(int value) {
        int[] values = getAlarmTimes();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        Log.e("unknown alarmTime: " + value);
        return 0;
    }

    private int[] getAlarmTimes() {
        String[] strs = getResources().getStringArray(R.array.alarmRuleAlarmTimeEntryValues);
        int[] values = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            values[i] = Integer.valueOf(strs[i]);
        }
        return values;
    }

    private int toAlarmTimeNoRingtone(int index) {
        return getAlarmTimesNoRingtone()[index];
    }

    private int toAlarmTimeNoRingtoneIndex(int value) {
        int[] values = getAlarmTimesNoRingtone();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        Log.e("unknown alarmTime: " + value);
        return 0;
    }

    private int[] getAlarmTimesNoRingtone() {
        String[] strs = getResources().getStringArray(R.array.alarmRuleAlarmTimeNoRingtoneEntryValues);
        int[] values = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            values[i] = Integer.valueOf(strs[i]);
        }
        return values;
    }

    @Override
    public void onQueryAllAlarmRules(Context context, Integer resultCode, List<AlarmRule> alarmRuleList) {
        // not used
    }

    @Override
    public void onDeleteAlarmRule(Context context, Integer resultCode) {
        // not used
    }

    @Override
    public void onUpdateAlarmRuleEnabled(Context context, Integer resultCode) {
        // not used
    }

}
