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
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.widget.DateRuleListViewer;
import com.isjfk.android.util.AndroidUtil;

/**
 * 查看工作日界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class WorkdayViewActivity extends Activity {

    public static final String WORKDAY_KEY = "workday";

    private boolean closeOnStop = false;
    private Workday workday = null;

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workday_view);

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
                Intent intent = new Intent(WorkdayViewActivity.this, WorkdayEditActivity.class);
                AndroidUtil.putExtra(intent, WorkdayEditActivity.MODE_KEY, WorkdayEditActivity.MODE_EDIT);
                AndroidUtil.putExtra(intent, WorkdayEditActivity.WORKDAY_KEY, workday);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();

        workday = AndroidUtil.getExtra(intent, WORKDAY_KEY);
        if (workday != null) {
            ((TextView) findViewById(R.id.workdayName)).setText(workday.getName());
            ((DateRuleListViewer) findViewById(R.id.dateRuleListViewer)).setDateRuleList(workday.getDateRuleList());
        } else {
            RACUtil.popupError(this, R.string.errWorkdayNoItemToView);
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
