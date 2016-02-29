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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmDataService.ResultCode;
import com.isjfk.android.rac.RegularAlarmDataServiceClient;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.callback.ConnectionCallback;
import com.isjfk.android.rac.callback.WorkdayCallback;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.rule.RuleDesc;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 工作日列表界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-2
 */
public class WorkdayListActivity extends ListActivity implements ConnectionCallback, WorkdayCallback {

    private RegularAlarmDataServiceClient client = new RegularAlarmDataServiceClient(this, this);

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workday_list);

        Button backButton = (Button) findViewById(R.id.titleBarBackButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button addButton = (Button) findViewById(R.id.titleBarAddButton);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                Intent intent = new Intent(WorkdayListActivity.this, WorkdayEditActivity.class);
                AndroidUtil.putExtra(intent, WorkdayEditActivity.MODE_KEY, WorkdayEditActivity.MODE_ADD);
                startActivity(intent);
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

        setListAdapter(new WorkdayAdapter(this, new ArrayList<Workday>()));
        ((TextView) findViewById(android.R.id.empty)).setText(R.string.loading);

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
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workday_options_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuPreferences:
            startActivity(new Intent(this, RACPreferenceActivity.class));
            return true;
        case R.id.menuHelp:
            RACUtil.openHelpActivity(this, "workday");
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
        getMenuInflater().inflate(R.menu.workday_list_context_menu, menu);
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Workday selectedWorkday = ((WorkdayAdapter) getListAdapter()).getWorkday(info.position);
        switch (item.getItemId()) {
        case R.id.contextMenuView:
            Intent intent = new Intent(this, WorkdayViewActivity.class);
            AndroidUtil.putExtra(intent, WorkdayViewActivity.WORKDAY_KEY, selectedWorkday);
            startActivity(intent);
            return true;
        case R.id.contextMenuDelete:
            client.deleteWorkday(selectedWorkday.getId());
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
        Workday workday = (Workday) getListView().getItemAtPosition(position);

        Intent intent = new Intent(this, WorkdayEditActivity.class);
        AndroidUtil.putExtra(intent, WorkdayEditActivity.MODE_KEY, WorkdayEditActivity.MODE_EDIT);
        AndroidUtil.putExtra(intent, WorkdayEditActivity.WORKDAY_KEY, workday);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.ConnectionCallback#onConnected(android.content.Context)
     */
    @Override
    public void onConnected(Context context) {
        client.queryAllWorkdays();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.WorkdayCallback#onQueryAllWorkdays(android.content.Context, java.lang.Integer, java.util.List)
     */
    @Override
    public void onQueryAllWorkdays(Context context, Integer resultCode, List<Workday> workdayList) {
        if (workdayList.isEmpty()) {
            ((TextView) findViewById(android.R.id.empty)).setText(R.string.workdayNoRecord);
        }
        showWorkdayList(workdayList);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.WorkdayCallback#onDeleteWorkday(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onDeleteWorkday(Context context, Integer resultCode) {
        if (ResultCode.SUCCESS.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.workdayDeleteSuccess);
            client.queryAllWorkdays();
        } else {
            RACUtil.popupError(this, R.string.errWorkdayDeleteFailed);
        }
    }

    /**
     * 将工作日显示在界面上。
     *
     * @param workdayList 工作日列表
     */
    private void showWorkdayList(List<Workday> workdayList) {
        setListAdapter(new WorkdayAdapter(this, workdayList));

        // FIXME: remove this restriction when locations added
        if (JavaUtil.isEmpty(workdayList)) {
            ((Button) findViewById(R.id.titleBarAddButton)).setVisibility(View.VISIBLE);
        } else {
            ((Button) findViewById(R.id.titleBarAddButton)).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 将工作日展示在ListView上的Adapter。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2011-7-31
     */
    class WorkdayAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<Workday> workdayList;

        public WorkdayAdapter(Context context, List<Workday> workdayList) {
            this.inflater = LayoutInflater.from(context);
            this.workdayList = workdayList;
        }

        @Override
        public int getCount() {
            return workdayList.size();
        }

        @Override
        public Object getItem(int position) {
            return getWorkday(position);
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
                    && (convertView.findViewById(R.id.workdayName) != null)
                    && (convertView.findViewById(R.id.dateRuleDesc) != null)) {
                itemView = convertView;
            }
            if (itemView == null) {
                itemView = inflater.inflate(R.layout.workday_list_item, null);
            }

            Workday workday = getWorkday(position);
            if (workday.isActived()) {
                ((TextView)itemView.findViewById(R.id.workdayName)).setTextColor(
                        getResources().getColorStateList(R.color.text_normal));
            } else {
                ((TextView)itemView.findViewById(R.id.workdayName)).setTextColor(
                        getResources().getColorStateList(R.color.text_dim2));
            }
            ((TextView)itemView.findViewById(R.id.workdayName)).setText(workday.getName());
            ((TextView)itemView.findViewById(R.id.dateRuleDesc)).setText(
                    RuleDesc.descSingleLine(getResources(), workday.getDateRuleList()));

            return itemView;
        }

        public Workday getWorkday(Integer index) {
            return workdayList.get(index);
        }

    }

    @Override
    public void onDisconnected(Context context) {
        // do nothing
    }

    @Override
    public void onAddWorkday(Context context, Integer resultCode, Workday workday) {
        // not used
    }

    @Override
    public void onUpdateWorkday(Context context, Integer resultCode) {
        // not used
    }

}
