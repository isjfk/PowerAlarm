/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmDataService.ResultCode;
import com.isjfk.android.rac.RegularAlarmDataServiceClient;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.callback.WorkdayCallback;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.common.RACUtil.SaveOnCloseAction;
import com.isjfk.android.rac.widget.DateRuleListEditor;
import com.isjfk.android.rac.widget.EditTextDefault;
import com.isjfk.android.util.AndroidUtil;

/**
 * 工作日详细信息界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class WorkdayEditActivity extends Activity implements WorkdayCallback {

    public static final String MODE_KEY = "mode";
    public static final String WORKDAY_KEY = "workday";

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;

    private int mode = MODE_ADD;
    private Workday workday = null;

    private EditTextDefault nameText;
    private DateRuleListEditor dateRuleEditor;

    private RegularAlarmDataServiceClient client = new RegularAlarmDataServiceClient(this, this);

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workday_edit);

        Button backButton = (Button) findViewById(R.id.titleBarBackButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        if (mode == MODE_ADD) {
            ((TextView) findViewById(R.id.titleBarText)).setText(R.string.workdayNewTitle);
            workday = BeanFactory.newWorkday();
        } else {
            ((TextView) findViewById(R.id.titleBarText)).setText(R.string.workdayEditTitle);
            workday = AndroidUtil.getExtra(intent, WORKDAY_KEY);
            if (workday == null) {
                RACUtil.popupError(this, R.string.errWorkdayNoItemToEdit);
                saveButton.setVisibility(View.INVISIBLE);
            }
        }

        if (workday != null) {
            nameText = (EditTextDefault) findViewById(R.id.workdayName);
            nameText.setText(workday.getName());
            dateRuleEditor = (DateRuleListEditor) findViewById(R.id.dateRuleListEditor);
            dateRuleEditor.setAtLeastOne(true);
            dateRuleEditor.setDateRuleList(workday.getDateRuleList());
        }
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
            RACUtil.openHelpActivity(this, "workday");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 保存修改并退出。
     */
    public void saveChanges() {
        workday.setName(nameText.getTextOrDefault());
        workday.setDateRuleList(dateRuleEditor.getDateRuleList());

        if (mode == MODE_ADD) {
            client.addWorkday(workday);
        } else {
            client.updateWorkday(workday);
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.WorkdayCallback#onAddWorkday(android.content.Context, java.lang.Integer, com.isjfk.android.rac.bean.Workday)
     */
    @Override
    public void onAddWorkday(Context context, Integer resultCode, Workday workday) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.workdayAddSuccess);
            finish();
        } else if (ResultCode.FAIL_LOCATION_USED.equals(resultCode)) {
            RACUtil.popupError(this, R.string.errWorkdayAddFailedLocationUsed);
        } else {
            RACUtil.popupError(this, R.string.errWorkdayAddFailed);
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.WorkdayCallback#onUpdateWorkday(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onUpdateWorkday(Context context, Integer resultCode) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.workdayUpdateSuccess);
            finish();
        } else if (ResultCode.FAIL_LOCATION_USED.equals(resultCode)) {
            RACUtil.popupError(this, R.string.errWorkdayUpdateFailedLocationUsed);
        } else {
            RACUtil.popupError(this, R.string.errWorkdayUpdateFailed);
        }
    }

    @Override
    public void onQueryAllWorkdays(Context context, Integer resultCode, List<Workday> workdayList) {
        // not used
    }

    @Override
    public void onDeleteWorkday(Context context, Integer resultCode) {
        // not used
    }

}
