/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmDataService.ResultCode;
import com.isjfk.android.rac.RegularAlarmDataServiceClient;
import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.callback.AlarmRuleCallback;
import com.isjfk.android.rac.callback.ConnectionCallback;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.rule.RuleDesc;
import com.isjfk.android.rac.widget.TimeView;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 闹铃规则列表界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-2
 */
public class AlarmRuleListActivity extends ListActivity implements ConnectionCallback, AlarmRuleCallback {

    private boolean closeOnStop = false;

    private RegularAlarmDataServiceClient client = new RegularAlarmDataServiceClient(this, this);

    private OnCheckedChangeListener enabledListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getTag() instanceof Integer) {
                client.updateAlarmRuleEnabled((Integer) buttonView.getTag(), isChecked);
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
        setContentView(R.layout.alarmrule_list);

        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeOnStop = true;
                Intent intent = new Intent(AlarmRuleListActivity.this, ScheduleListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        Button optionsMenuButton = (Button) findViewById(R.id.optionsMenuButton);
        optionsMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        Button addButton = (Button) findViewById(R.id.titleBarAddButton);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                Intent intent = new Intent(AlarmRuleListActivity.this, AlarmRuleEditActivity.class);
                AndroidUtil.putExtra(intent, AlarmRuleEditActivity.MODE_KEY, AlarmRuleEditActivity.MODE_ADD);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        registerForContextMenu(findViewById(android.R.id.list));
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        setListAdapter(new AlarmRuleAdapter(this, new ArrayList<AlarmRule>()));
        ((TextView) findViewById(android.R.id.empty)).setText(R.string.loading);
        closeOnStop = false;

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

        if (closeOnStop && !isFinishing()) {
            finish();
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuWorkday:
            startActivity(new Intent(this, WorkdayListActivity.class));
            return true;
        case R.id.menuPreferences:
            startActivity(new Intent(this, RACPreferenceActivity.class));
            return true;
        case R.id.menuHelp:
            RACUtil.openHelpActivity(this, "alarm");
            return true;
        case R.id.menuAbout:
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.alarmrule_list_context_menu, menu);
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        AlarmRule selectedAlarmRule = ((AlarmRuleAdapter) getListAdapter()).getAlarmRule(info.position);
        Intent intent = null;
        switch (item.getItemId()) {
        case R.id.contextMenuView:
            intent = new Intent(this, AlarmRuleViewActivity.class);
            AndroidUtil.putExtra(intent, AlarmRuleViewActivity.ALARMRULE_KEY, selectedAlarmRule);
            startActivity(intent);
            return true;
        case R.id.contextMenuClone:
            intent = new Intent(this, AlarmRuleEditActivity.class);
            AndroidUtil.putExtra(intent, AlarmRuleEditActivity.MODE_KEY, AlarmRuleEditActivity.MODE_ADD);
            AndroidUtil.putExtra(intent, AlarmRuleEditActivity.ALARMRULE_KEY, selectedAlarmRule);
            startActivity(intent);
            return true;
        case R.id.contextMenuDelete:
            client.deleteAlarmRule(selectedAlarmRule.getId());
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlarmRule alarmRule = (AlarmRule) getListView().getItemAtPosition(position);

        Intent intent = new Intent(this, AlarmRuleEditActivity.class);
        AndroidUtil.putExtra(intent, AlarmRuleEditActivity.MODE_KEY, AlarmRuleEditActivity.MODE_EDIT);
        AndroidUtil.putExtra(intent, AlarmRuleEditActivity.ALARMRULE_KEY, alarmRule);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.ConnectionCallback#onConnected(android.content.Context)
     */
    @Override
    public void onConnected(Context context) {
        client.queryAllAlarmRules();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.AlarmRuleCallback#onQueryAllAlarmRules(android.content.Context, java.lang.Integer, java.util.List)
     */
    @Override
    public void onQueryAllAlarmRules(Context context, Integer resultCode, List<AlarmRule> alarmRuleList) {
        if (alarmRuleList.isEmpty()) {
            ((TextView) findViewById(android.R.id.empty)).setText(R.string.alarmRuleNoRecord);
        }
        showAlarmRuleList(alarmRuleList);

        RACContext.setAdKeywordsForAlarmRule(alarmRuleList);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.AlarmRuleCallback#onDeleteAlarmRule(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onDeleteAlarmRule(Context context, Integer resultCode) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.alarmRuleDeleteSuccess);
            client.queryAllAlarmRules();
        } else {
            RACUtil.popupError(this, R.string.errAlarmRuleDeleteFailed);
        }
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.AlarmRuleCallback#onUpdateAlarmRuleEnabled(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onUpdateAlarmRuleEnabled(Context context, Integer resultCode) {
        if (ResultCode.SUCC_ENABLED.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.alarmRuleEnabled);
        } else if (ResultCode.SUCC_DISABLED.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.alarmRuleDisabled);
        } else if (ResultCode.FAILED.equals(resultCode)) {
            RACUtil.popupError(this, R.string.errAlarmRuleUpdateEnabledFailed);
        } else {
            String errMsg = "unknown alarm rule enabled update result: " + resultCode;
            Log.e(errMsg);
            throw new RACException(errMsg);
        }
    }

    /**
     * 将闹铃规则显示在界面上。
     *
     * @param alamrRuleList 闹铃规则列表
     */
    private void showAlarmRuleList(List<AlarmRule> alamrRuleList) {
        setListAdapter(new AlarmRuleAdapter(this, alamrRuleList));
    }

    /**
     * 将闹铃规则展示在ListView上的Adapter。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2011-8-31
     */
    class AlarmRuleAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<AlarmRule> alarmRuleList;

        public AlarmRuleAdapter(Context context, List<AlarmRule> alarmRuleList) {
            this.inflater = LayoutInflater.from(context);
            this.alarmRuleList = alarmRuleList;
        }

        @Override
        public int getCount() {
            return alarmRuleList.size();
        }

        @Override
        public Object getItem(int position) {
            return getAlarmRule(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = null;
            if ((convertView != null)
                    && (convertView instanceof LinearLayout)
                    && (convertView.findViewById(R.id.alarmRuleTime) != null)
                    && (convertView.findViewById(R.id.alarmRuleName) != null)
                    && (convertView.findViewById(R.id.alarmRuleDesc) != null)) {
                itemView = convertView;
            }
            if (itemView == null) {
                int itemId = R.layout.alarmrule_list_item;
                itemView = inflater.inflate(itemId, null);
            }

            AlarmRule alarmRule = getAlarmRule(position);

            TimeView timeView = (TimeView) itemView.findViewById(R.id.alarmRuleTime);
            if (alarmRule.isActived()) {
                timeView.setTextColor(getResources().getColorStateList(R.color.text_normal));
            } else {
                timeView.setTextColor(getResources().getColorStateList(R.color.text_dim2));
            }
            timeView.setTime(alarmRule.getTimeAsCalendar());

            ((TextView)itemView.findViewById(R.id.alarmRuleName)).setText(alarmRule.getName());
            ((TextView)itemView.findViewById(R.id.alarmRuleDesc)).setText(desc(alarmRule.getDateRuleList()));

            ToggleButton button = (ToggleButton)itemView.findViewById(R.id.alarmRuleEnabled);
            button.setOnCheckedChangeListener(null);    // 防止重用组件时调用Listener
            button.setTag(alarmRule.getId());
            button.setChecked(alarmRule.isEnabled());
            button.setOnCheckedChangeListener(enabledListener);

            return itemView;
        }

        public AlarmRule getAlarmRule(Integer index) {
            return alarmRuleList.get(index);
        }

    }

    private String desc(List<DateRule> ruleList) {
        String ruleDesc = RuleDesc.descSingleLine(getResources(), ruleList);
        if (JavaUtil.isEmpty(ruleDesc)) {
            ruleDesc = getResources().getString(R.string.alarmRuleNoRepeatRule);
        }
        return ruleDesc;
    }

    @Override
    public void onDisconnected(Context context) {
        // do nothing
    }

    @Override
    public void onAddAlarmRule(Context context, Integer resultCode, AlarmRule alarmRule) {
        // not used
    }

    @Override
    public void onUpdateAlarmRule(Context context, Integer resultCode) {
        // not used
    }

}
