/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.callback.AlarmRuleCallback;
import com.isjfk.android.rac.callback.ConnectionCallback;
import com.isjfk.android.rac.callback.ScheduleCallback;
import com.isjfk.android.rac.callback.WorkdayCallback;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * RegularAlarmDataService客户端。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-5
 */
public class RegularAlarmDataServiceClient {

    private Context context;
    private Object callback;

    private Messenger client = new Messenger(new ClientHandler());
    private Messenger server = null;
    private boolean isBind = false;

    private ServiceConnection srvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            server = new Messenger(service);

            // 绑定服务后调用回调方法
            if (callback instanceof ConnectionCallback) {
                ((ConnectionCallback) callback).onConnected(context);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBind = false;
            server = null;

            RACUtil.popupError(context, R.string.errServiceDisconnected);

            // 意外断开后调用回调方法
            if (callback instanceof ConnectionCallback) {
                ((ConnectionCallback) callback).onDisconnected(context);
            }
        }
    };

    /**
     * 构造RegularAlarmDataService客户端对象。
     *
     * @param context 应用上下文
     * @param callback 响应消息回调对象
     */
    public RegularAlarmDataServiceClient(Context context, Object callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * 绑定到RegularAlarmDataService。
     */
    public void bindService() {
        context.bindService(
                new Intent(context, RegularAlarmDataService.class),
                srvConn,
                Context.BIND_AUTO_CREATE);
        isBind = true;
    }

    /**
     * 解除绑定到RegularAlarmDataService。
     */
    public void unbindService() {
        if (isBind) {
            context.unbindService(srvConn);
            isBind = false;
        }
    }

    /**
     * 返回是否已绑定到RegularAlarmDataService。
     *
     * @return 如果已绑定到RegularAlarmDataService返回true，否则返回false
     */
    public boolean isBind() {
        return isBind;
    }

    /**
     * 检查是否已绑定到RegularAlarmDataService，如果没有则记录错误日志并显示错误消息。
     *
     * @return 如果已绑定到RegularAlarmDataService返回true，否则返回false
     */
    private boolean checkBind() {
        if (!isBind) {
            Log.e("RegularAlarmDataService not bind");
            RACUtil.popupError(context, R.string.errServiceNotBind);
        }
        return isBind;
    }

    /**
     * 查询所有闹铃日程。
     */
    public void queryAllSchedules() {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_SCHEDULE_QUERY_ALL));
        } catch (RemoteException e) {
            Log.e("error send query all schedule request", e);
            RACUtil.popupError(context, R.string.errScheduleQueryAllFailed);
        }
    }

    /**
     * 修改闹铃日程是否启用。
     *
     * @param id 闹铃日程ID
     * @param enabled 是否启用
     */
    public void updateScheduleEnabled(int id, boolean enabled) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_SCHEDULE_UPDATE_ENABLED, id, enabled));
        } catch (RemoteException e) {
            Log.e("error send update schedule enabled request", e);
            RACUtil.popupError(context, R.string.errScheduleUpdateEnabledFailed);
        }
    }

    /**
     * 查询所有闹铃规则。
     */
    public void queryAllAlarmRules() {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_ALARMRULE_QUERY_ALL));
        } catch (RemoteException e) {
            Log.e("error send query all alarm rule request", e);
            RACUtil.popupError(context, R.string.errAlarmRuleQueryAllFailed);
        }
    }

    /**
     * 增加闹铃规则。
     *
     * @param alarmRule 需要增加的闹铃规则
     */
    public void addAlarmRule(AlarmRule alarmRule) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_ALARMRULE_ADD, alarmRule));
        } catch (RemoteException e) {
            Log.e("error send add alarm rule request", e);
            RACUtil.popupError(context, R.string.errAlarmRuleAddFailed);
        }
    }

    /**
     * 删除闹铃规则。
     *
     * @param id 需要删除的闹铃规则ID
     */
    public void deleteAlarmRule(int id) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_ALARMRULE_DELETE, id));
        } catch (RemoteException e) {
            Log.e("error send delete alarm rule request", e);
            RACUtil.popupError(context, R.string.errAlarmRuleDeleteFailed);
        }
    }

    /**
     * 修改闹铃规则。
     *
     * @param alarmRule 需要修改的闹铃规则
     */
    public void updateAlarmRule(AlarmRule alarmRule) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_ALARMRULE_UPDATE, alarmRule));
        } catch (RemoteException e) {
            Log.e("error send update alarm rule request", e);
            RACUtil.popupError(context, R.string.errAlarmRuleUpdateFailed);
        }
    }

    /**
     * 修改闹铃规则是否启用。
     *
     * @param id 闹铃规则ID
     * @param enabled 是否启用
     */
    public void updateAlarmRuleEnabled(int id, boolean enabled) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_ALARMRULE_UPDATE_ENABLED, id, enabled));
        } catch (RemoteException e) {
            Log.e("error send update alarm rule enabled request", e);
            RACUtil.popupError(context, R.string.errAlarmRuleUpdateEnabledFailed);
        }
    }

    /**
     * 查询所有工作日。
     */
    public void queryAllWorkdays() {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_WORKDAY_QUERY_ALL));
        } catch (RemoteException e) {
            Log.e("error send query all workday request", e);
            RACUtil.popupError(context, R.string.errWorkdayQueryAllFailed);
        }
    }

    /**
     * 增加工作日。
     *
     * @param workday 需要增加的工作日
     */
    public void addWorkday(Workday workday) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_WORKDAY_ADD, workday));
        } catch (RemoteException e) {
            Log.e("error send add workday request", e);
            RACUtil.popupError(context, R.string.errWorkdayAddFailed);
        }
    }

    /**
     * 删除工作日。
     *
     * @param id 需要删除的工作日ID
     */
    public void deleteWorkday(int id) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_WORKDAY_DELETE, id));
        } catch (RemoteException e) {
            Log.e("error send delete workday request", e);
            RACUtil.popupError(context, R.string.errWorkdayDeleteFailed);
        }
    }

    /**
     * 修改工作日。
     *
     * @param workday 需要修改的工作日
     */
    public void updateWorkday(Workday workday) {
        if (!checkBind()) {
            return;
        }

        try {
            server.send(buildMsg(RegularAlarmDataService.MSG_WORKDAY_UPDATE, workday));
        } catch (RemoteException e) {
            Log.e("error send update workday request", e);
            RACUtil.popupError(context, R.string.errWorkdayUpdateFailed);
        }
    }

    private Message buildMsg(int what) {
        Message msg = Message.obtain(null, what);
        msg.replyTo = client;
        return msg;
    }

    private Message buildMsg(int what, Parcelable reqData) {
        Bundle data = new Bundle();
        data.putParcelable(RegularAlarmDataService.DATA_KEY, reqData);

        Message msg = Message.obtain(null, what);
        msg.replyTo = client;
        msg.setData(data);

        return msg;
    }

    private Message buildMsg(int what, int id) {
        Bundle data = new Bundle();
        data.putInt(RegularAlarmDataService.ID_KEY, id);

        Message msg = Message.obtain(null, what);
        msg.replyTo = client;
        msg.setData(data);

        return msg;
    }

    private Message buildMsg(int what, int id, boolean enabled) {
        Bundle data = new Bundle();
        data.putInt(RegularAlarmDataService.ID_KEY, id);
        data.putBoolean(RegularAlarmDataService.DATA_KEY, enabled);

        Message msg = Message.obtain(null, what);
        msg.replyTo = client;
        msg.setData(data);

        return msg;
    }

    /**
     * 处理RegularAlarmDataService的响应消息。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2011-7-31
     */
    class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Integer resultCode = (Integer) msg.getData().get(RegularAlarmDataService.RESULTCODE_KEY);
            switch (msg.what) {
            case RegularAlarmDataService.MSG_SCHEDULE_QUERY_ALL:
                if (callback instanceof ScheduleCallback) {
                    List<Schedule> schedules = getResultDataList(msg);
                    ((ScheduleCallback) callback).onQueryAllSchedules(context, resultCode, schedules);
                } else {
                    Log.e("no callback to handler onQueryAllSchedules");
                    RACUtil.popupError(context, R.string.errScheduleQueryAllFailed);
                }
                break;
            case RegularAlarmDataService.MSG_SCHEDULE_UPDATE_ENABLED:
                if (callback instanceof ScheduleCallback) {
                    Schedule schedule = getResultData(msg);
                    ((ScheduleCallback) callback).onUpdateScheduleEnabled(context, resultCode, schedule);
                } else {
                    Log.e("no callback to handler onUpdateScheduleEnabled");
                    RACUtil.popupError(context, R.string.errScheduleUpdateEnabledFailed);
                }
                break;
            case RegularAlarmDataService.MSG_ALARMRULE_QUERY_ALL:
                if (callback instanceof AlarmRuleCallback) {
                    List<AlarmRule> alarmRules = getResultDataList(msg);
                    ((AlarmRuleCallback) callback).onQueryAllAlarmRules(context, resultCode, alarmRules);
                } else {
                    Log.e("no callback to handler onQueryAllAlarmRules");
                    RACUtil.popupError(context, R.string.errAlarmRuleQueryAllFailed);
                }
                break;
            case RegularAlarmDataService.MSG_ALARMRULE_ADD:
                if (callback instanceof AlarmRuleCallback) {
                    AlarmRule newAlarmRule = (AlarmRule) getResultData(msg);
                    ((AlarmRuleCallback) callback).onAddAlarmRule(context, resultCode, newAlarmRule);
                } else {
                    Log.e("no callback to handler onAddAlarmRule");
                    RACUtil.popupError(context, R.string.errAlarmRuleAddFailed);
                }
                break;
            case RegularAlarmDataService.MSG_ALARMRULE_DELETE:
                if (callback instanceof AlarmRuleCallback) {
                    ((AlarmRuleCallback) callback).onDeleteAlarmRule(context, resultCode);
                } else {
                    Log.e("no callback to handler onDeleteAlarmRule");
                    RACUtil.popupError(context, R.string.errAlarmRuleDeleteFailed);
                }
                break;
            case RegularAlarmDataService.MSG_ALARMRULE_UPDATE:
                if (callback instanceof AlarmRuleCallback) {
                    ((AlarmRuleCallback) callback).onUpdateAlarmRule(context, resultCode);
                } else {
                    Log.e("no callback to handler onUpdateAlarmRule");
                    RACUtil.popupError(context, R.string.errAlarmRuleUpdateFailed);
                }
                break;
            case RegularAlarmDataService.MSG_ALARMRULE_UPDATE_ENABLED:
                if (callback instanceof AlarmRuleCallback) {
                    ((AlarmRuleCallback) callback).onUpdateAlarmRuleEnabled(context, resultCode);
                } else {
                    Log.e("no callback to handler onUpdateAlarmRuleEnabled");
                    RACUtil.popupError(context, R.string.errAlarmRuleUpdateEnabledFailed);
                }
                break;
            case RegularAlarmDataService.MSG_WORKDAY_QUERY_ALL:
                if (callback instanceof WorkdayCallback) {
                    List<Workday> workdays = getResultDataList(msg);
                    ((WorkdayCallback) callback).onQueryAllWorkdays(context, resultCode, workdays);
                } else {
                    Log.e("no callback to handler onQueryAllWorkdays");
                    RACUtil.popupError(context, R.string.errWorkdayQueryAllFailed);
                }
                break;
            case RegularAlarmDataService.MSG_WORKDAY_ADD:
                if (callback instanceof WorkdayCallback) {
                    Workday newWorkday = (Workday) getResultData(msg);
                    ((WorkdayCallback) callback).onAddWorkday(context, resultCode, newWorkday);
                } else {
                    Log.e("no callback to handler onAddWorkday");
                    RACUtil.popupError(context, R.string.errWorkdayAddFailed);
                }
                break;
            case RegularAlarmDataService.MSG_WORKDAY_DELETE:
                if (callback instanceof WorkdayCallback) {
                    ((WorkdayCallback) callback).onDeleteWorkday(context, resultCode);
                } else {
                    Log.e("no callback to handler onDeleteWorkday");
                    RACUtil.popupError(context, R.string.errWorkdayDeleteFailed);
                }
                break;
            case RegularAlarmDataService.MSG_WORKDAY_UPDATE:
                if (callback instanceof WorkdayCallback) {
                    ((WorkdayCallback) callback).onUpdateWorkday(context, resultCode);
                } else {
                    Log.e("no callback to handler onUpdateWorkday");
                    RACUtil.popupError(context, R.string.errWorkdayUpdateFailed);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
            }
        }

        private <T extends Parcelable> T getResultData(Message msg) {
            if (msg.getData().containsKey(RegularAlarmDataService.RESULTDATA_KEY)) {
                return msg.getData().getParcelable(RegularAlarmDataService.RESULTDATA_KEY);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private <T extends Parcelable> List<T> getResultDataList(Message msg) {
            List<T> resultDataList = null;
            if (msg.getData().containsKey(RegularAlarmDataService.RESULTDATA_KEY)) {
                Parcelable[] resultArr = msg.getData().getParcelableArray(RegularAlarmDataService.RESULTDATA_KEY);
                resultDataList = (List<T>) JavaUtil.toList(resultArr);
            }
            return resultDataList;
        }
    }

}
