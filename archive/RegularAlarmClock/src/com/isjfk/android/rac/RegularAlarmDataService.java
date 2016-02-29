/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;

import com.isjfk.android.rac.bean.AlarmRule;
import com.isjfk.android.rac.bean.Config.Columns.KeyEnum;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.Workday;
import com.isjfk.android.rac.common.DaoFactory;
import com.isjfk.android.rac.common.Log;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.rac.common.RACTimeUtil;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.rac.dao.AlarmRuleDao;
import com.isjfk.android.rac.dao.ConfigDao;
import com.isjfk.android.rac.dao.DateRuleDao;
import com.isjfk.android.rac.dao.ScheduleDao;
import com.isjfk.android.rac.dao.WorkdayDao;
import com.isjfk.android.util.JavaUtil;
import com.isjfk.android.util.WeakReferenceHandler;

/**
 * 规则闹钟数据服务。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-7-22
 */
public class RegularAlarmDataService extends Service {

    // 请求消息ID
    public static final int MSG_SCHEDULE_QUERY_ALL = 11;
    public static final int MSG_SCHEDULE_UPDATE_ENABLED = 12;
    public static final int MSG_ALARMRULE_QUERY_ALL = 21;
    public static final int MSG_ALARMRULE_ADD = 22;
    public static final int MSG_ALARMRULE_DELETE = 23;
    public static final int MSG_ALARMRULE_UPDATE = 24;
    public static final int MSG_ALARMRULE_UPDATE_ENABLED = 25;
    public static final int MSG_WORKDAY_QUERY_ALL = 31;
    public static final int MSG_WORKDAY_ADD = 32;
    public static final int MSG_WORKDAY_DELETE = 33;
    public static final int MSG_WORKDAY_UPDATE = 34;

    // 请求消息参数KEY
    public static final String ID_KEY = "id";
    public static final String DATA_KEY = "data";

    // 响应消息参数KEY
    public static final String RESULTCODE_KEY = "resultCode";
    public static final String RESULTDATA_KEY = "resultData";

    // 响应消息结果码
    public static interface ResultCode {
        Integer FAIL_LOCATION_USED = -2;
        Integer FAILED = -1;
        Integer SUCCESS = 0;
        Integer SUCC_ENABLED = 1;
        Integer SUCC_DISABLED = 2;
    }

    protected ConfigDao configDao = DaoFactory.getInstance().getConfigDao();
    protected ScheduleDao scheduleDao = DaoFactory.getInstance().getScheduleDao();
    protected AlarmRuleDao alarmRuleDao = DaoFactory.getInstance().getAlarmRuleDao();
    protected WorkdayDao workdayDao = DaoFactory.getInstance().getWorkdayDao();
    protected DateRuleDao dateRuleDao = DaoFactory.getInstance().getDateRuleDao();

    protected final Messenger server = new Messenger(new RegularAlarmDataHandler(this));

    /**
     * {@inheritDoc}
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startId) {
        stopSelf(startId);
    }

    /**
     * {@inheritDoc}
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return server.getBinder();
    }

    /**
     * 处理客户端发送的请求消息，并返回响应消息。
     *
     * @author Jimmy F. Klarke
     * @version 1.0, 2011-7-31
     */
    private static class RegularAlarmDataHandler extends WeakReferenceHandler<RegularAlarmDataService> {
        public RegularAlarmDataHandler(RegularAlarmDataService service) {
            super(service);
        }

        @Override
        public void onMessage(RegularAlarmDataService service, Message msg) {
            int resultCode;
            Object resultData = null;

            switch (msg.what) {
            case MSG_SCHEDULE_QUERY_ALL:
                resultCode = ResultCode.SUCCESS;
                resultData = service.queryAllSchedule();
                service.sendReply(msg, ResultCode.SUCCESS, resultData);
                break;
            case MSG_SCHEDULE_UPDATE_ENABLED:
                resultCode = service.updateScheduleEnabled(
                        msg.getData().getInt(ID_KEY),
                        msg.getData().getBoolean(DATA_KEY));
                resultData = service.querySchedule(msg.getData().getInt(ID_KEY));
                service.sendReply(msg, resultCode, resultData);
                break;
            case MSG_ALARMRULE_QUERY_ALL:
                resultCode = ResultCode.SUCCESS;
                resultData = service.queryAllAlarmRule();
                service.sendReply(msg, resultCode, resultData);
                break;
            case MSG_ALARMRULE_ADD:
                resultCode = service.addAlarmRule((AlarmRule) msg.getData().getParcelable(DATA_KEY));
                if (resultCode > 0) {
                    resultData = service.queryAlarmRule(resultCode);
                    resultCode = ResultCode.SUCCESS;
                }
                service.sendReply(msg, resultCode, resultData);
                break;
            case MSG_ALARMRULE_DELETE:
                resultCode = service.deleteAlarmRule(msg.getData().getInt(ID_KEY));
                service.sendReply(msg, resultCode, null);
                break;
            case MSG_ALARMRULE_UPDATE:
                resultCode = service.updateAlarmRule((AlarmRule) msg.getData().getParcelable(DATA_KEY));
                service.sendReply(msg, resultCode, null);
                break;
            case MSG_ALARMRULE_UPDATE_ENABLED:
                resultCode = service.updateAlarmRuleEnabled(
                        msg.getData().getInt(ID_KEY),
                        msg.getData().getBoolean(DATA_KEY));
                service.sendReply(msg, resultCode, null);
                break;
            case MSG_WORKDAY_QUERY_ALL:
                resultCode = ResultCode.SUCCESS;
                resultData = service.queryAllWorkday();
                service.sendReply(msg, resultCode, resultData);
                break;
            case MSG_WORKDAY_ADD:
                resultCode = service.addWorkday((Workday) msg.getData().getParcelable(DATA_KEY));
                if (resultCode > 0) {
                    resultData = service.queryWorkday(resultCode);
                    resultCode = ResultCode.SUCCESS;
                }
                service.sendReply(msg, resultCode, resultData);
                break;
            case MSG_WORKDAY_DELETE:
                resultCode = service.deleteWorkday(msg.getData().getInt(ID_KEY));
                service.sendReply(msg, resultCode, null);
                break;
            case MSG_WORKDAY_UPDATE:
                resultCode = service.updateWorkday((Workday) msg.getData().getParcelable(DATA_KEY));
                service.sendReply(msg, resultCode, null);
                break;
            default:
                super.handleMessage(msg);
                break;
            }
        }
    }

    /**
     * 发送响应消息。
     *
     * @param msg 请求消息
     * @param replyCode 响应结果码
     * @param replyData 响应结果数据
     */
    protected void sendReply(Message msg, Integer replyCode, Object replyData) {
        if (msg.replyTo != null) {
            Bundle data = new Bundle();
            data.setClassLoader(getClassLoader());
            if (replyCode != null) {
                data.putInt(RESULTCODE_KEY, replyCode);
            }
            if (replyData != null) {
                if (replyData instanceof Parcelable) {
                    data.putParcelable(RESULTDATA_KEY, (Parcelable) replyData);
                } else if (replyData instanceof Parcelable[]) {
                    data.putParcelableArray(RESULTDATA_KEY, (Parcelable[]) replyData);
                } else if (replyData instanceof Boolean) {
                    data.putBoolean(RESULTDATA_KEY, (Boolean) replyData);
                } else if (replyData instanceof Integer) {
                    data.putInt(RESULTDATA_KEY, (Integer) replyData);
                } else {
                    String errMsg = "unknown data type: " + replyData.getClass().getName();
                    Log.e(errMsg);
                    throw new RACException(errMsg);
                }
            }

            Message replyMsg = Message.obtain(null, msg.what);
            replyMsg.setData(data);

            try {
                msg.replyTo.send(replyMsg);
            } catch (RemoteException e) {
                String errMsg = "error send reply message";
                Log.e(errMsg, e);
                throw new RACException(errMsg, e);
            }
        } else {
            String errMsg = "no replyTo found to send reply message";
            Log.e(errMsg);
            throw new RACException(errMsg);
        }
    }

    /**
     * 查询闹铃日程。
     *
     * @param id 闹铃日程ID
     * @return 闹铃日程，如果不存在返回null
     */
    protected Schedule querySchedule(int id) {
        return scheduleDao.query(getContentResolver(), id);
    }

    /**
     * 查询所有闹铃日程，查询时进行闹铃日程的刷新。
     *
     * @return 所有闹铃日程
     */
    protected synchronized Schedule[] queryAllSchedule() {
        expandSchedules();
        List<Schedule> scheduleList = queryAllScheduleNoRefresh();
        removeExpiredSchedules(scheduleList, RACContext.getScheduleForceDelHour());
        sortScheduleList(scheduleList);
        return scheduleList.toArray(new Schedule[scheduleList.size()]);
    }

    /**
     * 查询所有闹铃日程，查询时不进行闹铃日程的刷新。
     *
     * @return 所有闹铃日程
     */
    protected synchronized List<Schedule> queryAllScheduleNoRefresh() {
        List<Schedule> scheduleList = scheduleDao.queryAll(getContentResolver());
        sortScheduleList(scheduleList);
        return scheduleList;
    }

    /**
     * 查找下一个可以响铃的闹铃日程。
     *
     * @return 下一个可以响铃的闹铃日程，如果找不到则返回null
     */
    protected Schedule findNextRingableSchedule() {
        return RACUtil.findNextRingableSchedule(queryAllScheduleNoRefresh());
    }

    /**
     * 查找所有可以响铃的闹铃日程。
     *
     * @return 所有可以响铃的闹铃日程，如果找不到则返回空List
     */
    protected List<Schedule> findRingableScheduleList() {
        return RACUtil.findRingableScheduleList(queryAllScheduleNoRefresh());
    }

    /**
     * 将闹铃日程扩展到配置的长度。
     */
    private void expandSchedules() {
        Calendar startDate = genScheduleStartDate();
        Calendar endDate = genScheduleEndDate();

        Calendar realStartDate = loadScheduleEndDate();
        if (realStartDate == null) {
            Log.e("load previous schedule end date from config failed, rebuild all schedules");
            rebuildSchedules();
            return;
        }

        // 上次已保存的闹铃日程结束日期和当前结束日期一致，不需要再次生成
        if (realStartDate.equals(endDate)) {
            return;
        }

        realStartDate.add(Calendar.DAY_OF_MONTH, 1);
        if ((realStartDate.compareTo(startDate) >= 0) && (realStartDate.compareTo(endDate) <= 0)) {
            List<Schedule> scheduleList = RegularAlarmAlgorithm.genScheduleList(
                    JavaUtil.toList(queryAllAlarmRule()),
                    JavaUtil.toList(queryAllWorkday()),
                    scheduleDao.queryAll(getContentResolver()),
                    realStartDate,
                    endDate);
            scheduleDao.add(getContentResolver(), scheduleList);
            saveScheduleEndDate(endDate);
        } else {
            Log.e("real schedule start date "
                    + RACTimeUtil.stdFormatDate(realStartDate)
                    + "not within range ["
                    + RACTimeUtil.stdFormatDate(startDate)
                    + RACTimeUtil.stdFormatDate(endDate)
                    + "], rebuild all schedules");
            rebuildSchedules();
            return;
        }
    }

    /**
     * 删除闹铃日程中已过期的记录。
     *
     * @param scheduleList 闹铃日程列表，按响铃时间排序
     * @param expireHour 过期时间，单位：小时。
     */
    private void removeExpiredSchedules(List<Schedule> scheduleList, int expireHour) {
        Calendar currTime = Calendar.getInstance();
        currTime.set(Calendar.SECOND, 0);
        currTime.set(Calendar.MILLISECOND, 0);

        Calendar expireTime = (Calendar) currTime.clone();
        expireTime.add(Calendar.HOUR_OF_DAY, -expireHour);

        boolean ringableChanged = false;
        Iterator<Schedule> iter = scheduleList.iterator();
        while (iter.hasNext()) {
            Schedule schedule = iter.next();
            if (schedule.getTime().compareTo(currTime) >= 0) {
                break;
            }

            if (!RACUtil.isRingable(schedule)) {
                scheduleDao.delete(getContentResolver(), schedule.getId());
                iter.remove();
                break;
            } else if ((schedule.getTime().compareTo(expireTime) < 0)) {
                Log.e("ringable schedule " + schedule.getId() + " expired, force remove it");
                scheduleDao.delete(getContentResolver(), schedule.getId());
                iter.remove();
                ringableChanged = true;
                break;
            }
        }

        if (ringableChanged) {
            sendScheduleChanged();
        }
    }

    /**
     * 将闹铃日程按响铃时间排序。
     *
     * @param scheduleList 闹铃日程
     * @param 如果修改了日程顺序返回true，否则返回false
     */
    private void sortScheduleList(List<Schedule> scheduleList) {
        int size = scheduleList.size();

        // 只有闹铃被snoozed之后才可能造成顺序打乱。snooze时间不会超过1小时，所以重排序最近1天的闹铃日程就足够了
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DAY_OF_MONTH, 1);
        RACUtil.clearTime(endTime);

        for (int i = 0; i < (size - 1); i++) {
            Calendar date1 = scheduleList.get(i).getTime();
            if (date1.compareTo(endTime) > 0) {
                break;
            }

            for (int j = (i + 1); j < size; j++) {
                Calendar date2 = scheduleList.get(j).getTime();
                if (date2.compareTo(endTime) > 0) {
                    break;
                }

                if (date1.compareTo(date2) > 0) {
                    Schedule tmp = scheduleList.get(i);
                    scheduleList.set(i, scheduleList.get(j));
                    scheduleList.set(j, tmp);
                    date1 = date2;
                }
            }
        }
    }

    /**
     * 修改闹铃日程是否启用。
     *
     * @param id 闹铃日程ID
     * @param enabled 是否启用
     * @return 见ResultCode中的结果码
     */
    protected int updateScheduleEnabled(int id, boolean enabled) {
        Schedule prevRingSchedule = findNextRingableSchedule();
        boolean isSuccess = scheduleDao.updateEnabled(getContentResolver(), id, enabled) == 1;
        if (isSuccess) {
            checkNextRingChanged(prevRingSchedule);
            return enabled ? ResultCode.SUCC_ENABLED : ResultCode.SUCC_DISABLED;
        } else {
            return ResultCode.FAILED;
        }
    }

    /**
     * 刷新闹铃日程actived状态。
     * 工作日或当前位置改变后需要调用此方法修改闹铃日程的actived状态。
     */
    protected void refreshSchedulesActived() {
        List<Schedule> scheduleList = queryAllScheduleNoRefresh();
        List<AlarmRule> alarmRuleList = JavaUtil.toList(queryAllAlarmRule());
        List<Workday> workdayList = JavaUtil.toList(queryAllWorkday());

        Schedule prevRingSchedule = findNextRingableSchedule();

        RegularAlarmAlgorithm.setScheduleListActived(scheduleList, alarmRuleList, workdayList);
        for (Schedule schedule : scheduleList) {
            scheduleDao.updateActived(getContentResolver(), schedule.getId(), schedule.isActived());
        }

        checkNextRingChanged(prevRingSchedule);
    }

    /**
     * 重新构造闹铃日程。
     */
    protected void rebuildSchedules() {
        Calendar startDate = genScheduleStartDate();
        Calendar endDate = genScheduleEndDate();
        List<Schedule> scheduleList = RegularAlarmAlgorithm.genScheduleList(
                JavaUtil.toList(queryAllAlarmRule()),
                JavaUtil.toList(queryAllWorkday()),
                scheduleDao.queryAll(getContentResolver()),
                startDate,
                endDate);
        scheduleDao.deleteAll(getContentResolver());
        scheduleDao.add(getContentResolver(), scheduleList);
        saveScheduleEndDate(endDate);

        checkNextRingChanged(null);
    }

    /**
     * 检查下次响铃的闹铃日程是否有变化，如果有变化则重设闹铃。
     *
     * @param prevRingSchedule 闹铃日程修改前，下次响铃的日程
     */
    private void checkNextRingChanged(Schedule prevRingSchedule) {
        Schedule nextRingSchedule = findNextRingableSchedule();
        if (!RACUtil.isSameId(prevRingSchedule, nextRingSchedule)) {
            sendScheduleChanged();
        }
    }

    /**
     * 重设闹铃。
     */
    private void sendScheduleChanged() {
        Intent intent = new Intent(this, RegularAlarmService.class);
        intent.setAction(RegularAlarmService.ACTION_SCHEDULE_CHANGED);
        startService(intent);
    }

    /**
     * 查询所有闹铃规则。
     *
     * @return 所有闹铃规则
     */
    protected AlarmRule[] queryAllAlarmRule() {
        List<AlarmRule> alarmRuleList = alarmRuleDao.queryAll(getContentResolver());
        for (AlarmRule alarmRule : alarmRuleList) {
            loadAlarmRuleData(alarmRule);
        }
        return alarmRuleList.toArray(new AlarmRule[alarmRuleList.size()]);
    }

    /**
     * 查询闹铃规则。
     *
     * @param id 闹铃规则ID
     * @return 闹铃规则，如果不存在返回null
     */
    protected AlarmRule queryAlarmRule(int id) {
        AlarmRule alarmRule = alarmRuleDao.query(getContentResolver(), id);
        if (alarmRule != null) {
            loadAlarmRuleData(alarmRule);
        }
        return alarmRule;
    }

    /**
     * 构造闹铃规则数据。
     *
     * @param alarmRule 闹铃规则
     */
    protected void loadAlarmRuleData(AlarmRule alarmRule) {
        alarmRule.setDateRuleList(queryDateRule(alarmRule.getDateRuleId()));
        // FIXME: query location
    }

    /**
     * 增加闹铃规则。
     *
     * @param alarmRule 闹铃规则
     * @return 见ResultCode中的结果码
     */
    protected int addAlarmRule(AlarmRule alarmRule) {
        int dateRuleId = addDateRule(alarmRule.getDateRuleList());
        if (dateRuleId == -1) {
            Log.e("error add daterule");
            return ResultCode.FAILED;
        }
        alarmRule.setDateRuleId(dateRuleId);

        int result = alarmRuleDao.add(getContentResolver(), alarmRule);
        rebuildSchedules();

        return result;
    }

    /**
     * 删除闹铃规则。
     *
     * @param id 闹铃规则ID
     * @return 见ResultCode中的结果码
     */
    protected int deleteAlarmRule(int id) {
        AlarmRule alarmRule = queryAlarmRule(id);
        if (!deleteDateRule(alarmRule.getDateRuleId())) {
            Log.e("error delete daterule " + id);
        }

        if (alarmRuleDao.delete(getContentResolver(), id) == 1) {
            rebuildSchedules();
            return ResultCode.SUCCESS;
        }

        return ResultCode.FAILED;
    }

    /**
     * 修改闹铃规则。
     *
     * @param alarmRule 闹铃规则
     * @return 见ResultCode中的结果码
     */
    protected int updateAlarmRule(AlarmRule alarmRule) {
        Integer dateRuleId = updateDateRule(alarmRule.getDateRuleId(), alarmRule.getDateRuleList());
        if (dateRuleId == -1) {
            Log.e("error update daterule");
            return ResultCode.FAILED;
        }
        alarmRule.setDateRuleId(dateRuleId);

        if (alarmRuleDao.update(getContentResolver(), alarmRule) == 1) {
            rebuildSchedules();
            return ResultCode.SUCCESS;
        } else {
            Log.e("error update alarm rule");
            return ResultCode.FAILED;
        }
    }

    /**
     * 修改闹铃规则是否启用。
     *
     * @param id 闹铃规则ID
     * @param enabled 是否启用
     * @return 见ResultCode中的结果码
     */
    protected int updateAlarmRuleEnabled(int id, boolean enabled) {
        boolean isSuccess = alarmRuleDao.updateEnabled(getContentResolver(), id, enabled) == 1;
        if (isSuccess) {
            rebuildSchedules();
            return enabled ? ResultCode.SUCC_ENABLED : ResultCode.SUCC_DISABLED;
        } else {
            return ResultCode.FAILED;
        }
    }

    /**
     * 查询所有工作日。
     *
     * @return 所有工作日
     */
    protected Workday[] queryAllWorkday() {
        List<Workday> workdayList = workdayDao.queryAll(getContentResolver());
        for (Workday workday : workdayList) {
            loadWorkdayData(workday);
        }
        return workdayList.toArray(new Workday[workdayList.size()]);
    }

    /**
     * 查询工作日。
     *
     * @param id 工作日ID
     * @return 工作日，如果不存在则返回null
     */
    protected Workday queryWorkday(int id) {
        Workday workday = workdayDao.query(getContentResolver(), id);
        if (workday != null) {
            loadWorkdayData(workday);
        }
        return workday;
    }

    /**
     * 构造工作日数据。
     *
     * @param workday 工作日
     */
    protected void loadWorkdayData(Workday workday) {
        workday.setDateRuleList(queryDateRule(workday.getDateRuleId()));
        // FIXME: query location
    }

    /**
     * 增加工作日。
     *
     * @param workday 工作日
     * @return 见ResultCode中的结果码
     */
    protected int addWorkday(Workday workday) {
        if (!workdayDao.locationUsable(getContentResolver(), workday.getLocationId())) {
            Log.e("location of id " + workday.getLocationId() + " already used");
            return ResultCode.FAIL_LOCATION_USED;
        }

        int dateRuleId = addDateRule(workday.getDateRuleList());
        if (dateRuleId == -1) {
            Log.e("error add daterule");
            return ResultCode.FAILED;
        }
        workday.setDateRuleId(dateRuleId);

        int result = workdayDao.add(getContentResolver(), workday);
        refreshSchedulesActived();

        return result;
    }

    /**
     * 删除工作日。
     *
     * @param id 工作日ID
     * @return 见ResultCode中的结果码
     */
    protected int deleteWorkday(int id) {
        Workday workday = queryWorkday(id);
        if (!deleteDateRule(workday.getDateRuleId())) {
            Log.e("error delete daterule " + id);
        }

        if (workdayDao.delete(getContentResolver(), id) == 1) {
            refreshSchedulesActived();
            return ResultCode.SUCCESS;
        }

        return ResultCode.FAILED;
    }

    /**
     * 修改工作日。
     *
     * @param workday 工作日
     * @return 见ResultCode中的结果码
     */
    protected int updateWorkday(Workday workday) {
        if (!workdayDao.locationUsable(getContentResolver(), workday.getId(), workday.getLocationId())) {
            Log.e("location of id " + workday.getLocationId() + " already used");
            return ResultCode.FAIL_LOCATION_USED;
        }

        Integer dateRuleId = updateDateRule(workday.getDateRuleId(), workday.getDateRuleList());
        if (dateRuleId == -1) {
            Log.e("error update daterule");
            return ResultCode.FAILED;
        }
        workday.setDateRuleId(dateRuleId);

        if (workdayDao.update(getContentResolver(), workday) == 1) {
            refreshSchedulesActived();
            return ResultCode.SUCCESS;
        } else {
            Log.e("error update workday");
            return ResultCode.FAILED;
        }
    }

    /**
     * 查询一组日期规则。
     *
     * @param gid 日期规则组ID
     * @return 日期规则
     */
    protected List<DateRule> queryDateRule(int gid) {
        return dateRuleDao.query(getContentResolver(), gid);
    }

    /**
     * 增加一组日期规则。
     *
     * @param dateRules 日期规则
     * @return 增加的日期规则组ID。0表示日期规则列表为空；-1表示增加失败
     */
    protected int addDateRule(List<DateRule> dateRuleList) {
        return dateRuleDao.add(getContentResolver(), dateRuleList);
    }

    /**
     * 删除一组日期规则。
     *
     * @param gid 日期规则组ID
     * @return true表示删除成功，false表示删除失败
     */
    protected boolean deleteDateRule(int gid) {
        int size = dateRuleDao.getSize(getContentResolver(), gid);
        return dateRuleDao.delete(getContentResolver(), gid) == size;
    }

    /**
     * 修改一组日期规则。
     *
     * @param gid 日期规则组ID
     * @param dateRuleList 新的日期规则
     * @return 修改后的日期规则组ID。0表示日期规则列表为空；-1表示增加失败
     */
    protected Integer updateDateRule(int gid, List<DateRule> dateRuleList) {
        if (!deleteDateRule(gid)) {
            Log.e("error delete daterule " + gid);
        }

        return dateRuleDao.add(getContentResolver(), gid, dateRuleList);
    }

    /**
     * 从配置项中获取已生成的闹铃日程截止日期。
     *
     * @return 闹铃日程截止日期，如果无法获取则返回null
     */
    private Calendar loadScheduleEndDate() {
        String endDateStr = configDao.get(getContentResolver(), KeyEnum.ScheduleEndDate, null);
        if (JavaUtil.isEmpty(endDateStr)) {
            return null;
        }

        Calendar endDateCal = RACTimeUtil.stdParseDate(endDateStr);
        if (endDateCal == null) {
            return null;
        }

        RACUtil.clearTime(endDateCal);
        return endDateCal;
    }

    /**
     * 将闹铃日程截止日期保存到配置项。
     *
     * @param endDate 闹铃日程截止日期
     */
    private void saveScheduleEndDate(Calendar endDate) {
        configDao.set(getContentResolver(), KeyEnum.ScheduleEndDate, RACTimeUtil.stdFormatDate(endDate));
    }

    /**
     * 生成闹铃日程开始日期。
     *
     * @return 闹铃日程开始日期
     */
    private Calendar genScheduleStartDate() {
        Calendar startDate = Calendar.getInstance();
        RACUtil.clearTime(startDate);
        return startDate;
    }

    /**
     * 生成闹铃日程截止日期。
     *
     * @return 闹铃日程截止日期
     */
    private Calendar genScheduleEndDate() {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, RACContext.getScheduleDays());
        RACUtil.clearTime(endDate);
        return endDate;
    }

}
