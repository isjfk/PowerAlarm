/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.activity.HelpActivity;
import com.isjfk.android.rac.bean.Schedule;
import com.isjfk.android.rac.bean.other.DayHourMinute;
import com.isjfk.android.rac.bean.other.RingtoneConfig;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 规则闹铃工具类。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-6
 */
public class RACUtil {

    private static final long MILL2MINUTES = 1000 * 60;
    private static final long MINUTES2HOUR = 60;
    private static final long HOURS2DAY = 24;

    /**
     * 判断闹铃日程是否生效（是否应该响铃）。
     *
     * @param schedule 闹铃日程
     * @return 生效返回true，否则返回false
     */
    public static boolean isRingable(Schedule schedule) {
        return schedule.isActived() && schedule.isEnabled();
    }

    /**
     * 判断闹铃日程是否处于snoozed状态。
     *
     * @param schedule 闹铃日程
     * @return 闹铃日程处于snoozed状态返回true，否则返回false
     */
    public static boolean isSnoozed(Schedule schedule) {
        return schedule.getTimeoutTimes() != 0;
    }

    /**
     * 判断闹铃日程是否已过期。
     *
     * @param schedule 闹铃日程
     * @return 已过期返回true，否则返回false
     */
    public static boolean isExpired(Schedule schedule) {
        return Calendar.getInstance().compareTo(schedule.getTime()) > 0;
    }

    /**
     * 查找下一个可以响铃的闹铃日程。
     *
     * @param scheduleList 闹铃日程列表
     * @return 下一个可以响铃的闹铃日程，如果找不到则返回null
     */
    public static Schedule findNextRingableSchedule(List<Schedule> scheduleList) {
        for (Schedule schedule : scheduleList) {
            if (isRingable(schedule)) {
                return schedule;
            }
        }
        return null;
    }

    /**
     * 查找所有可以响铃的闹铃日程。
     *
     * @param scheduleList 闹铃日程列表
     * @return 所有可以响铃的闹铃日程，如果找不到则返回空List
     */
    public static List<Schedule> findRingableScheduleList(List<Schedule> scheduleList) {
        List<Schedule> ringableScheduleList = new ArrayList<Schedule>(scheduleList.size());
        for (Schedule schedule : scheduleList) {
            if (isRingable(schedule)) {
                ringableScheduleList.add(schedule);
            }
        }
        return ringableScheduleList;
    }

    /**
     * 判断闹铃日程和ID是否一致。
     *
     * @param schedule 闹铃日程
     * @param id ID
     * @return ID一致返回true，否则返回false
     */
    public static boolean isSameId(Schedule schedule, int id) {
        if (schedule != null) {
            return schedule.getId() == id;
        }
        return false;
    }

    /**
     * 判断两个闹铃日程ID是否一致。
     *
     * @param schedule1 闹铃日程1
     * @param schedule2 闹铃日程2
     * @return 两个闹铃日程都不为null且ID一致返回true，否则返回false
     */
    public static boolean isSameId(Schedule schedule1, Schedule schedule2) {
        if ((schedule1 != null) && (schedule2 != null)) {
            return schedule1.getId() == schedule2.getId();
        }
        return false;
    }

    /**
     * 调整分钟的边界。
     *
     * @param time 时间
     */
    public static void adjustMinuteSegment(Calendar time) {
        int segment = RACContext.getTimeMinuteSegment();
        int minute = time.get(Calendar.MINUTE);
        minute = (minute / segment + 1) * segment;
        time.set(Calendar.MINUTE, minute);
    }

    /**
     * 获取时间所表示的星期名称。
     *
     * @param res 应用资源
     * @param time 时间
     * @return 时间所表示的星期名称
     */
    public static String getDayOfWeekName(Resources res, Calendar time) {
        int dayOfWeek = time.get(Calendar.DAY_OF_WEEK);
        if ((dayOfWeek >= Calendar.SUNDAY) && (dayOfWeek <= Calendar.SATURDAY)) {
            return res.getStringArray(R.array.dayOfWeekName)[dayOfWeek - 1];
        } else {
            Log.e("unknown dayOfWeek: " + dayOfWeek);
        }
        return res.getString(R.string.ruleDescUnknown);
    }

    /**
     * 获取时间所表示的星期简称。
     *
     * @param res 应用资源
     * @param time 时间
     * @return 时间所表示的星期简称
     */
    public static String getDayOfWeekNameShort(Resources res, Calendar time) {
        int dayOfWeek = time.get(Calendar.DAY_OF_WEEK);
        if ((dayOfWeek >= Calendar.SUNDAY) && (dayOfWeek <= Calendar.SATURDAY)) {
            return res.getStringArray(R.array.dayOfWeekNameShort)[dayOfWeek - 1];
        } else {
            Log.e("unknown dayOfWeek: " + dayOfWeek);
        }
        return res.getString(R.string.ruleDescUnknown);
    }

    /**
     * 判断时间是不是上午。
     *
     * @param time 时间
     * @return 上午返回true，否则返回false
     */
    public static boolean isAM(Calendar time) {
        return Calendar.AM == time.get(Calendar.AM_PM);
    }

    /**
     * 将Calendar对象的时、分、秒、毫秒设置为0。
     *
     * @param cal Calendar对象
     */
    public static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 获取响铃时间在小睡之后的下次响铃时间。
     *
     * @param time 响铃时间
     * @return 小睡之后的下次响铃时间
     */
    public static Calendar getSnoozedTime(Calendar time) {
        Calendar snoozedTime = (Calendar) time.clone();
        snoozedTime.add(Calendar.MINUTE, RACContext.getSnoozeTime());
        return snoozedTime;
    }

    /**
     * 获取下次响铃到现在的间隔时间。
     *
     * @param nextAlarmTime 下次响铃时间
     * @return 下次响铃到现在的间隔时间
     */
    public static DayHourMinute getNextAlarmInterval(Calendar nextAlarmTime) {
        long currTime = Calendar.getInstance().getTimeInMillis();
        long ringTime = nextAlarmTime.getTimeInMillis();

        long interval = ringTime - currTime;

        long millis = interval % MILL2MINUTES;
        interval = interval / MILL2MINUTES;
        interval += (millis > 0) ? 1 : 0;

        long minutes = interval % MINUTES2HOUR;
        interval = interval / MINUTES2HOUR;

        long hours = interval % HOURS2DAY;
        long days = interval / HOURS2DAY;

        return new DayHourMinute((int)days, (int)hours, (int)minutes);
    }



    /**
     * 获取铃声。
     *
     * @param context Context
     * @param ringtoneUri 铃声Uri
     * @return Android铃声，如果不存在则返回null
     */
    public static Ringtone getRingtone(Context context, String ringtoneUri) {
        if (JavaUtil.isEmpty(ringtoneUri)) {
            return null;
        }
        return getRingtone(context, Uri.parse(ringtoneUri));
    }

    /**
     * 获取铃声。
     *
     * @param context Context
     * @param ringtoneUri 铃声Uri
     * @return Android铃声，如果不存在则返回null
     */
    public static Ringtone getRingtone(Context context, Uri ringtoneUri) {
        if (ringtoneUri == null) {
            return null;
        }
        return RingtoneManager.getRingtone(context, ringtoneUri);
    }

    /**
     * 检查铃声是否可用。
     *
     * @param context Context
     * @param ringtoneUri 铃声Uri
     * @return 如果铃声可用返回true，否则返回false
     */
    public static boolean isRingtoneValid(Context context, String ringtoneUri) {
        return getRingtone(context, ringtoneUri) != null;
    }

    /**
     * 检查铃声是否可用。
     *
     * @param context Context
     * @param ringtoneUri 铃声Uri
     * @return 如果铃声可用返回true，否则返回false
     */
    public static boolean isRingtoneValid(Context context, Uri ringtoneUri) {
        return getRingtone(context, ringtoneUri) != null;
    }

    /**
     * 获取铃声名称。
     *
     * @param context Context
     * @param ringtoneConfig 铃声配置
     * @return 铃声名称，如果找不到则返回null
     */
    public static String getRingtoneName(Context context, RingtoneConfig ringtoneConfig) {
        if (ringtoneConfig == null) {
            return null;
        }

        if (ringtoneConfig.isPreference()) {
            return context.getString(R.string.ringtoneTypePreference);
        } else if (ringtoneConfig.isDefault()) {
            return context.getString(R.string.ringtoneTypeDefault);
        }

        Ringtone ringtone = getRingtone(context, ringtoneConfig.getRingtone());
        if (ringtone != null) {
            return ringtone.getTitle(context);
        }
        return null;
    }



    /**
     * 长时间显示弹出提示消息。
     *
     * @param context Context
     * @param resId 弹出提示消息资源ID
     */
    public static void popupTips(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示弹出错误消息。
     *
     * @param context Context
     * @param resId 弹出错误消息资源ID
     */
    public static void popupError(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示弹出通知消息。
     *
     * @param context Context
     * @param resId 弹出通知消息资源ID
     */
    public static void popupNotify(Context context, int resId) {
        if (RACContext.isPopupNotification()) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 长时间显示弹出通知消息。
     *
     * @param context Context
     * @param msg 弹出通知消息
     */
    public static void popupNotifyLong(Context context, String msg) {
        if (RACContext.isPopupNotification()) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 请求父控件不要拦截MotionEvent事件。
     */
    public static void requestMotionEvent(View view) {
        ScrollView scrollView = RACUtil.findParentScrollView(view);
        if (scrollView != null) {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * 恢复父控件对MotionEvent事件的拦截。
     */
    public static void releaseMotionEvent(View view) {
        ScrollView scrollView = RACUtil.findParentScrollView(view);
        if (scrollView != null) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        }
    }

    /**
     * 查找类型为ScrollView的父控件。
     *
     * @param view 开始查找的控件
     * @return 类型为ScrollView的父控件，找不到则返回null
     */
    public static ScrollView findParentScrollView(View view) {
        if (view != null) {
            if (view instanceof ScrollView) {
                return (ScrollView) view;
            } else if (view.getParent() instanceof View) {
                return findParentScrollView((View) view.getParent());
            }
        }
        return null;
    }

    /**
     * 首次启动时打开帮助。
     * 如果是应用安装后首次启动，打开帮助界面。
     * 如果是升级后首次启动，则打开What's New对话框。
     *
     * @param context
     */
    public static void openFirstStartupHelp(Context context) {
        if (RACContext.isShowFirstStartupHelp()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.openHelpConfirmation);
            builder.setPositiveButton(R.string.buttonYes, new HelpActivityListener(context));
            builder.setNegativeButton(R.string.buttonNo, null);
            builder.show();
        } else if (RACContext.isShowWhatsNew()) {
            openWhatsNewDialog(context);
        }
    }

    private static class HelpActivityListener implements OnClickListener {
        private Context context;
        public HelpActivityListener(Context context) {
            this.context = context;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            openHelpActivity(context, null);
        }
    }

    /**
     * 打开帮助界面。
     *
     * @param context 应用上下文
     * @param anchor 需要打开的帮助章节
     */
    public static void openHelpActivity(Context context, String anchor) {
        Intent intent = new Intent(context, HelpActivity.class);
        if (JavaUtil.isNotEmpty(anchor)) {
            AndroidUtil.putExtra(intent, HelpActivity.HELP_ANCHOR_KEY, anchor);
        }
        context.startActivity(intent);
    }

    /**
     * 打开What's New对话框。
     *
     * @param context 应用上下文
     */
    public static void openWhatsNewDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.whatsnew);

        WhatsNewDialogListener listener = new WhatsNewDialogListener(dialog);

        WebView webContent = (WebView) dialog.findViewById(R.id.webContent);
        webContent.setWebViewClient(listener);
        webContent.loadUrl("file:///android_asset/whatsnew/index.html");

        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(listener);

        dialog.show();
    }

    private static class WhatsNewDialogListener extends WebViewClient implements View.OnClickListener {
        private Dialog dialog;
        public WhatsNewDialogListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            dialog.setTitle(view.getTitle());
        }
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }

    /**
     * “是否保存更改”提示窗口。
     *
     * @param action 保存更改操作
     */
    public static void saveOnCloseConfirm(SaveOnCloseAction action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(action.getContext());
        builder.setMessage(R.string.saveConfirmation);
        builder.setPositiveButton(R.string.buttonYes, new SaveCofirmedListener(action, true));
        builder.setNegativeButton(R.string.buttonNo, new SaveCofirmedListener(action, false));
        builder.show();
    }

    private static class SaveCofirmedListener implements DialogInterface.OnClickListener {
        private SaveOnCloseAction action;
        private boolean save;
        public SaveCofirmedListener(SaveOnCloseAction action, boolean save) {
            this.action = action;
            this.save = save;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (save) {
                action.save();
            }
            action.close();
        }
    }

    public abstract static class SaveOnCloseAction {
        private Context context;
        public SaveOnCloseAction(Context context) {
            this.context = context;
        }
        public Context getContext() {
            return context;
        }
        public abstract void save();
        public abstract void close();
    }

}
