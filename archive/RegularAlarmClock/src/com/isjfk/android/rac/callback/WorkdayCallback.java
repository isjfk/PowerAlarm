/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.callback;

import java.util.List;

import android.content.Context;

import com.isjfk.android.rac.bean.Workday;

/**
 * 工作日回调接口。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-5
 */
public interface WorkdayCallback {

    /**
     * 查询所有工作日的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param workdayList 工作日列表
     */
    public void onQueryAllWorkdays(Context context, Integer resultCode, List<Workday> workdayList);

    /**
     * 增加工作日的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param workday 新增加的工作日
     */
    public void onAddWorkday(Context context, Integer resultCode, Workday workday);

    /**
     * 修改工作日的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     */
    public void onUpdateWorkday(Context context, Integer resultCode);

    /**
     * 删除工作日的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     */
    public void onDeleteWorkday(Context context, Integer resultCode);

}
