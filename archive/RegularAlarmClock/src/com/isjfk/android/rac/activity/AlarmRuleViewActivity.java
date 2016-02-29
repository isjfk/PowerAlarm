/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.widget.DateRuleListViewer;
import com.isjfk.android.util.AndroidUtil;

/**
 * 查看闹铃规则界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class AlarmRuleViewActivity extends Activity {

    public static final String ALARMRULE_KEY = "alarmRule";

    private boolean closeOnStop = false;
    private AlarmRule alarmRule = null;

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmrule_view);

        Button backButton = (Button) findViewById(R.id.titleBarBackButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button editButton = (Button) findViewById(R.id.titleBarEditButton);
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOnStop = true;
                Intent intent = new Intent(AlarmRuleViewActivity.this, AlarmRuleEditActivity.class);
                AndroidUtil.putExtra(intent, AlarmRuleEditActivity.MODE_KEY, AlarmRuleEditActivity.MODE_EDIT);
                AndroidUtil.putExtra(intent, AlarmRuleEditActivity.ALARMRULE_KEY, alarmRule);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();

        alarmRule = AndroidUtil.getExtra(intent, ALARMRULE_KEY);
        if (alarmRule != null) {
            ((TextView) findViewById(R.id.alarmRuleName)).setText(alarmRule.getName());
            ((TextView) findViewById(R.id.alarmRuleTime)).setText(RACTimeUtil.formatTime(alarmRule.getTimeAsCalendar()));
            ((DateRuleListViewer) findViewById(R.id.dateRuleListViewer)).setDateRuleList(alarmRule.getDateRuleList());
            ((LinearLayout) findViewById(R.id.alarmRuleDelAfterExpiredGroup)).setVisibility(
                    alarmRule.isExpireable() ? View.VISIBLE : View.GONE);
            ((TextView) findViewById(R.id.alarmRuleDelAfterExpired)).setText(
                    alarmRule.isDelAfterExpired() ? R.string.alarmRuleDelAfterExpiredTrue : R.string.alarmRuleDelAfterExpiredFalse);
            ((LinearLayout) findViewById(R.id.alarmRuleComplyWorkdayGroup)).setVisibility(
                    alarmRule.isOneTime() ? View.GONE : View.VISIBLE);
            ((TextView) findViewById(R.id.alarmRuleComplyWorkday)).setText(
                    alarmRule.isComplyWorkday() ? R.string.alarmRuleComplyWorkdayTrue : R.string.alarmRuleComplyWorkdayFalse);
            ((TextView) findViewById(R.id.alarmRuleRing)).setText(
                    getResources().getStringArray(R.array.alarmRuleRing)[alarmRule.getRing()]);
            ((TextView) findViewById(R.id.alarmRuleRingtone)).setText(RACUtil.getRingtoneName(this, alarmRule.getRingtone()));
            ((TextView) findViewById(R.id.alarmRuleVibrate)).setText(
                    getResources().getStringArray(R.array.alarmRuleVibrate)[alarmRule.getVibrate()]);
        } else {
            RACUtil.popupError(this, R.string.errAlarmRuleNoItemToView);
            editButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        closeOnStop = false;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (closeOnStop && !isFinishing()) {
            finish();
        }
    }

}
