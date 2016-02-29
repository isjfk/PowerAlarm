package com.isjfk.android.rac.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.RegularAlarmDataService.ResultCode;
import com.isjfk.android.rac.RegularAlarmDataServiceClient;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.other.DayHourMinute;
import com.isjfk.android.rac.callback.ConnectionCallback;
import com.isjfk.android.rac.callback.ScheduleCallback;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.widget.TimeView;
import com.isjfk.android.util.JavaUtil;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 闹铃日程列表界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-1
 */
public class ScheduleListActivity extends ListActivity implements ConnectionCallback, ScheduleCallback {

    private final static int MSG_REFRESH_LIST = 1;

    private RegularAlarmDataServiceClient client = new RegularAlarmDataServiceClient(this, this);

    private OnCheckedChangeListener enabledListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getTag() instanceof Integer) {
                client.updateScheduleEnabled((Integer) buttonView.getTag(), isChecked);
            }
        }
    };

//    private OnCheckedChangeListener dismissListener = new OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (buttonView.getTag() instanceof Integer) {
//                Integer scheduleId = (Integer) buttonView.getTag();
//                Schedule targetSchedule = null;
//                for (Schedule schedule : ((ScheduleAdapter) getListAdapter()).getScheduleList()) {
//                    if (scheduleId.equals(schedule.getId())) {
//                        targetSchedule = schedule;
//                    }
//                }
//
//                Intent intent = new Intent(ScheduleListActivity.this, DismissSnoozedConfirmActivity.class);
//                AndroidUtil.putExtra(intent, RegularAlarmService.SCHEDULE, targetSchedule);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                startActivityForResult(intent, REQ_DISMISS_SNOOZED);
//            }
//        }
//    };

    private Handler refreshHandler = new RefreshHandler(this);

    /**
     * 刷新Handler。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2012-8-12
     */
    private static class RefreshHandler extends WeakReferenceHandler<ScheduleListActivity> {
        public RefreshHandler(ScheduleListActivity activity) {
            super(activity);
        }

        public void onMessage(ScheduleListActivity activity, Message msg) {
            switch (msg.what) {
            case MSG_REFRESH_LIST:
                ((ScheduleAdapter) activity.getListAdapter()).notifyDataSetChanged();

                Calendar currTime = Calendar.getInstance();
                Calendar nextTime = (Calendar) currTime.clone();
                nextTime.add(Calendar.MINUTE, 1);
                nextTime.set(Calendar.SECOND, 0);
                nextTime.set(Calendar.MILLISECOND, 100);
                long delayTime = nextTime.getTimeInMillis() - currTime.getTimeInMillis();

                sendEmptyMessageDelayed(MSG_REFRESH_LIST, delayTime);
                break;
            }
        }
    };

    /**
     * 构造闹铃日程Activity。
     */
    public ScheduleListActivity() {
        super();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list);

        RACContext.resetAlarm(this);

        Button alarmRuleButton = (Button) findViewById(R.id.alarmRuleButton);
        alarmRuleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View paramView) {
                Intent intent = new Intent(ScheduleListActivity.this, AlarmRuleListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button optionsMenuButton = (Button) findViewById(R.id.optionsMenuButton);
        optionsMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
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

        setListAdapter(new ScheduleAdapter(this, new ArrayList<Schedule>()));
        ((TextView) findViewById(android.R.id.empty)).setText(R.string.loading);

        client.bindService();

        RACUtil.openFirstStartupHelp(this);
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        refreshHandler.removeMessages(MSG_REFRESH_LIST);
        client.unbindService();
    }

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
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
            RACUtil.openHelpActivity(this, null);
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
     * @see com.isjfk.android.rac.callback.ConnectionCallback#onConnected(android.content.Context)
     */
    @Override
    public void onConnected(Context context) {
        client.queryAllSchedules();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.ScheduleCallback#onQueryAllSchedules(android.content.Context, java.lang.Integer, java.util.List)
     */
    @Override
    public void onQueryAllSchedules(Context context, Integer resultCode, List<Schedule> scheduleList) {
        if (scheduleList.isEmpty()) {
            ((TextView) findViewById(android.R.id.empty)).setText(R.string.scheduleNoRecord);
        }
        showScheduleList(scheduleList);
        refreshHandler.sendEmptyMessage(MSG_REFRESH_LIST);

        RACContext.setAdKeywordsForSchedule(scheduleList);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.callback.ScheduleCallback#onUpdateScheduleEnabled(android.content.Context, java.lang.Integer)
     */
    @Override
    public void onUpdateScheduleEnabled(Context context, Integer resultCode, Schedule schedule) {
        if (ResultCode.SUCC_ENABLED.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.scheduleEnabled);
            updateSchedule(schedule);
        } else if (ResultCode.SUCC_DISABLED.equals(resultCode)) {
            RACUtil.popupNotify(this, R.string.scheduleDisabled);
            updateSchedule(schedule);
        } else if (ResultCode.FAILED.equals(resultCode)) {
            RACUtil.popupError(this, R.string.errScheduleUpdateEnabledFailed);
        } else {
            String errMsg = "unknown schedule enabled update result: " + resultCode;
            Log.e(errMsg);
            throw new RACException(errMsg);
        }
    }

    /**
     * 将闹铃日程显示在界面上。
     *
     * @param schedules 闹铃日程列表
     */
    private void showScheduleList(List<Schedule> schedules) {
        setListAdapter(new ScheduleAdapter(this, schedules));
    }

    /**
     * 更新视图中的闹铃日程。
     *
     * @param schedule 闹铃日程
     */
    private void updateSchedule(Schedule schedule) {
        ((ScheduleAdapter) getListAdapter()).updateSchedule(schedule);
    }

    /**
     * 将闹铃日程展示在ListView上的Adapter。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2011-7-31
     */
    class ScheduleAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<Schedule> scheduleList;
        private Schedule nextRingSchedule;

        public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
            this.inflater = LayoutInflater.from(context);

            if (RACContext.isShowInactiveSchedules()) {
                setScheduleList(scheduleList);
            } else {
                // 只显示可响铃的闹铃日程
                List<Schedule> ringableList = new ArrayList<Schedule>(scheduleList.size());
                for (Schedule schedule : scheduleList) {
                    if (schedule.isActived()) {
                        ringableList.add(schedule);
                    }
                }
                setScheduleList(ringableList);
            }
        }

        @Override
        public int getCount() {
            return getScheduleList().size();
        }

        @Override
        public Object getItem(int position) {
            return getScheduleList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Schedule schedule = getScheduleList().get(position);

            int listItemId = -1;
            if (schedule == nextRingSchedule) {
                listItemId = R.layout.schedule_list_item_nextalarm;
            } else {
                listItemId = R.layout.schedule_list_item;
            }

            View itemView = null;
            if ((convertView != null) && (convertView.getId() == listItemId)) {
                itemView = convertView;
            }
            if (itemView == null) {
                itemView = inflater.inflate(listItemId, null);
            }

            String date = RACTimeUtil.formatDate(schedule.getTime());
            String dayOfWeek = RACUtil.getDayOfWeekNameShort(getResources(), schedule.getTime());
            String dateText = getResources().getString(R.string.scheduleDateText, date, dayOfWeek);

            ColorStateList timeTextColor = null;
            if (schedule == nextRingSchedule) {
                timeTextColor = getResources().getColorStateList(R.color.text_nextalarm);
            } else if (schedule.isActived()) {
                if (schedule.isEnabled()) {
                    timeTextColor = getResources().getColorStateList(R.color.text_normal);
                } else {
                    timeTextColor = getResources().getColorStateList(R.color.text_dim2);
                }
            } else {
                timeTextColor = getResources().getColorStateList(R.color.text_dim3);
            }

            TimeView timeView = (TimeView) itemView.findViewById(R.id.ringTime);
            timeView.setTextColor(timeTextColor);
            timeView.setTime(schedule.getTime());

            ((TextView)itemView.findViewById(R.id.ringDate)).setText(dateText);
            ((TextView)itemView.findViewById(R.id.ringName)).setText(schedule.getName());

            TextView nextAlarmInterval = (TextView) itemView.findViewById(R.id.nextAlarmInterval);
            if (nextAlarmInterval != null) {
                DayHourMinute interval = RACUtil.getNextAlarmInterval(schedule.getTime());
                if (interval.day == 0) {
                    nextAlarmInterval.setText(getResources().getString(
                                    R.string.scheduleNextAlarmIntervalHourMinute,
                                    String.valueOf(interval.hour),
                                    String.valueOf(interval.minute)));
                } else {
                    nextAlarmInterval.setText(getResources().getString(
                                    R.string.scheduleNextAlarmIntervalDayHourMinute,
                                    String.valueOf(interval.day),
                                    String.valueOf(interval.hour),
                                    String.valueOf(interval.minute)));
                }
            }

            ToggleButton button = (ToggleButton)itemView.findViewById(R.id.ringEnabled);
            button.setOnCheckedChangeListener(null);    // 防止重用组件时调用Listener
            button.setTag(schedule.getId());
            button.setChecked(schedule.isEnabled());
            button.setOnCheckedChangeListener(enabledListener);
//            if (RACUtil.isSnoozed(schedule)) {
//                button.setOnCheckedChangeListener(dismissListener);
//            } else {
//                button.setOnCheckedChangeListener(enabledListener);
//            }

            return itemView;
        }

        public List<Schedule> getScheduleList() {
            return scheduleList;
        }

        private void setScheduleList(List<Schedule> scheduleList) {
            this.scheduleList = scheduleList;
            this.nextRingSchedule = RACUtil.findNextRingableSchedule(scheduleList);
        }

        /**
         * 更新闹铃日程。
         *
         * @param schedule 需要更新的闹铃日程
         */
        public void updateSchedule(Schedule schedule) {
            if (JavaUtil.isEmpty(scheduleList) || (schedule == null)) {
                return;
            }

            for (int i = 0; i < scheduleList.size(); i++) {
                if (scheduleList.get(i).getId() == schedule.getId()) {
                    scheduleList.set(i, schedule);

                    setScheduleList(scheduleList);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

    }

    @Override
    public void onDisconnected(Context context) {
        // do nothing
    }

}
