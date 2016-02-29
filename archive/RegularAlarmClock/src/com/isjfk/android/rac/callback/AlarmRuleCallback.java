/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.callback;

import java.util.List;

import android.content.Context;

import com.isjfk.android.rac.bean.AlarmRule;

/**
 * 闹铃规则回调接口。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-5
 */
public interface AlarmRuleCallback {

    /**
     * 查询所有闹铃规则的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param alarmRuleList 闹铃规则列表
     */
    public void onQueryAllAlarmRules(Context context, Integer resultCode, List<AlarmRule> alarmRuleList);

    /**
     * 增加闹铃规则的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     * @param alarmRule 新增加的闹铃规则
     */
    public void onAddAlarmRule(Context context, Integer resultCode, AlarmRule alarmRule);

    /**
     * 修改闹铃规则的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     */
    public void onUpdateAlarmRule(Context context, Integer resultCode);

    /**
     * 删除闹铃规则的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     */
    public void onDeleteAlarmRule(Context context, Integer resultCode);

    /**
     * 修改闹铃规则是否启用状态的回调方法。
     *
     * @param context 应用上下文
     * @param resultCode 见ResultCode中的结果码
     */
    public void onUpdateAlarmRuleEnabled(Context context, Integer resultCode);

}
