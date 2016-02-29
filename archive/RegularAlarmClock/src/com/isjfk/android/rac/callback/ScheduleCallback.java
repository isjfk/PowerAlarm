/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.callback;

import java.util.List;

import android.content.Context;

import com.isjfk.android.rac.bean.Schedule;

/**
 * 闹铃日程回调接口。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-5
 */
public interface ScheduleCallback {

    /**
     * 查询所有闹铃日程的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param scheduleList 闹铃日程列表
     */
    public void onQueryAllSchedules(Context context, Integer resultCode, List<Schedule> scheduleList);

    /**
     * 修改闹铃日程是否启用状态的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param schedule 修改后的闹铃日程
     */
    public void onUpdateScheduleEnabled(Context context, Integer resultCode, Schedule schedule);

}
