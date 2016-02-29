/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.bean.other.DayMonth;
import com.isjfk.android.rac.bean.other.DayMonthYear;
import com.isjfk.android.rac.bean.rule.AlarmXOffYDaysRule;
import com.isjfk.android.rac.bean.rule.DayMonthRangeRule;
import com.isjfk.android.rac.bean.rule.DayMonthYearListRule;
import com.isjfk.android.rac.bean.rule.DayMonthYearRangeRule;
import com.isjfk.android.rac.bean.rule.DayOfMonthRule;
import com.isjfk.android.rac.bean.rule.DayOfWeekInMonthRule;
import com.isjfk.android.rac.bean.rule.DayOfWeekRule;
import com.isjfk.android.rac.bean.rule.EveryDayRule;
import com.isjfk.android.rac.bean.rule.RevDayOfWeekInMonthRule;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.RACContext;
import com.isjfk.android.rac.common.RACException;
import com.isjfk.android.rac.widget.DayMonthYearPicker.OnDayMonthYearChangedListener;

/**
 * 日期规则修改对话框。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-14
 */
public class EditRuleDialog {

    private DateRuleListEditor editor;
    private DateRule dateRule;
    private View dialogView;
    private AlertDialog dialog;

    private OnClickListener okListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int itemIndex) {
            editor.replaceDateRule(dateRule, getDateRule());
        }
    };

    private OnItemSelectedListener ruleTypeListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int dateRuleType = toDateRuleType(position);
            if (dateRule.getRuleType() == dateRuleType) {
                setDateRule(dateRule);
            } else {
                setDateRule(BeanFactory.newDateRule(dateRuleType));
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Spinner ruleTypeSpinner = (Spinner) parent;
            ruleTypeSpinner.setSelection(RuleTypeEnum.EveryDay);
        }
    };

    public EditRuleDialog(DateRuleListEditor editor, DateRule dateRule) {
        this.editor = editor;
        this.dateRule = dateRule;

        dialogView = LayoutInflater.from(editor.getContext()).inflate(R.layout.daterule_edit_rule, null);
        Spinner ruleTypeSpinner = (Spinner) dialogView.findViewById(R.id.ruleType);
        ruleTypeSpinner.setSelection(toDateRuleTypeIndex(dateRule.getRuleType()));
        ruleTypeSpinner.setOnItemSelectedListener(ruleTypeListener);
        setDateRule(dateRule);

        AlertDialog.Builder builder = new AlertDialog.Builder(editor.getContext());
        builder.setTitle(R.string.dialogEditRuleTitle);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.buttonOK, okListener);
        builder.setNegativeButton(R.string.buttonCancel, null);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();

        // clear FLAG_ALT_FOCUSABLE_IM since we need im to be shown to edit some rule through EditText
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private DateRule getDateRule() {
        DateRule newDateRule = dateRule;

        Spinner ruleTypeSpinner = (Spinner) dialogView.findViewById(R.id.ruleType);
        int newRuleType = toDateRuleType(ruleTypeSpinner.getSelectedItemPosition());
        if (dateRule.getRuleType() != newRuleType) {
            newDateRule = BeanFactory.newDateRule(newRuleType);
            newDateRule.setEMode(dateRule.getEMode());
        }

        if (newDateRule instanceof DayOfWeekRule) {
            ((DayOfWeekRule) newDateRule).setDayOfWeekList(getDayOfWeekRule());
        } else if (newDateRule instanceof DayOfMonthRule) {
            getDayOfMonthRule((DayOfMonthRule) newDateRule);
        } else if (newDateRule instanceof DayOfWeekInMonthRule) {
            getDayOfWeekInMonthRule((DayOfWeekInMonthRule) newDateRule);
        } else if (newDateRule instanceof RevDayOfWeekInMonthRule) {
            getRevDayOfWeekInMonthRule((RevDayOfWeekInMonthRule) newDateRule);
        } else if (newDateRule instanceof DayMonthRangeRule) {
            getDayMonthRangeRule((DayMonthRangeRule) newDateRule);
        } else if (newDateRule instanceof DayMonthYearRangeRule) {
            getDayMonthYearRangeRule((DayMonthYearRangeRule) newDateRule);
        } else if (newDateRule instanceof AlarmXOffYDaysRule) {
            getAlarmXOffYDaysRule((AlarmXOffYDaysRule) newDateRule);
        } else if (newDateRule instanceof DayMonthYearListRule) {
            getDayMonthYearListRule((DayMonthYearListRule) newDateRule);
        }

        return newDateRule;
    }

    private void setDateRule(DateRule dateRule) {
        if (dateRule instanceof EveryDayRule) {
            setDateRuleView(null);
        } else if (dateRule instanceof DayOfWeekRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dow);
            setDayOfWeekRule(((DayOfWeekRule) dateRule).getDayOfWeekList());
        } else if (dateRule instanceof DayOfMonthRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dom);
            setDayOfMonthRule((DayOfMonthRule) dateRule);
        } else if (dateRule instanceof DayOfWeekInMonthRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dowim);
            setDayOfWeekInMonthRule((DayOfWeekInMonthRule) dateRule);
        } else if (dateRule instanceof RevDayOfWeekInMonthRule) {
            setDateRuleView(R.layout.daterule_edit_rule_revdowim);
            setRevDayOfWeekInMonthRule((RevDayOfWeekInMonthRule) dateRule);
        } else if (dateRule instanceof DayMonthRangeRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dmr);
            setDayMonthRangeRule((DayMonthRangeRule) dateRule);
        } else if (dateRule instanceof DayMonthYearRangeRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dmyr);
            setDayMonthYearRangeRule((DayMonthYearRangeRule) dateRule);
        } else if (dateRule instanceof AlarmXOffYDaysRule) {
            setDateRuleView(R.layout.daterule_edit_rule_axoyd);
            setAlarmXOffYDaysRule((AlarmXOffYDaysRule) dateRule);
        } else if (dateRule instanceof DayMonthYearListRule) {
            setDateRuleView(R.layout.daterule_edit_rule_dmyl);
            setDayMonthYearListRule((DayMonthYearListRule) dateRule);
        }
    }

    private void setDateRuleView(Integer viewId) {
        ScrollView viewContainer = (ScrollView) dialogView.findViewById(R.id.dateRuleView);
        LayoutInflater inflater = LayoutInflater.from(editor.getContext());

        viewContainer.removeAllViews();
        if (viewId != null) {
            viewContainer.addView(inflater.inflate(viewId, null));
        }
    }

    private int toDateRuleType(int typeIndex) {
        return getDateRuleTypes()[typeIndex];
    }

    private int toDateRuleTypeIndex(int dateRuleType) {
        int[] types = getDateRuleTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] == dateRuleType) {
                return i;
            }
        }
        throw new RACException("unknown date rule type: " + dateRuleType);
    }

    private int[] getDateRuleTypes() {
        String[] typesStr = dialogView.getResources().getStringArray(R.array.dateRuleType);
        int[] types = new int[typesStr.length];
        for (int i = 0; i < typesStr.length; i++) {
            types[i] = Integer.valueOf(typesStr[i]);
        }
        return types;
    }

    private List<Integer> getDayOfWeekRule() {
        List<Integer> dayOfWeekList = new ArrayList<Integer>();
        ViewGroup dayOfWeekView = (ViewGroup) dialogView.findViewById(R.id.dayOfWeek);

        if (RACContext.isFirstDaySunday()) {
            for (int i = 0; i < 7; i++) {
                if (((CheckBox) dayOfWeekView.getChildAt(i)).isChecked()) {
                    dayOfWeekList.add(i + 1);
                }
            }
        } else {
            for (int i = 1; i < 8; i++) {
                if (((CheckBox) dayOfWeekView.getChildAt(i)).isChecked()) {
                    dayOfWeekList.add((i % 7) + 1);
                }
            }
        }

        return dayOfWeekList;
    }

    private void setDayOfWeekRule(List<Integer> dayOfWeekList) {
        ViewGroup dayOfWeekView = (ViewGroup) dialogView.findViewById(R.id.dayOfWeek);

        if (RACContext.isFirstDaySunday()) {
            dayOfWeekView.findViewById(R.id.sundayFirst).setVisibility(View.VISIBLE);
            dayOfWeekView.findViewById(R.id.sundayLast).setVisibility(View.GONE);
            for (int i = 0; i < 7; i++) {
                ((CheckBox) dayOfWeekView.getChildAt(i)).setChecked(dayOfWeekList.contains(i + 1));
            }
        } else {
            dayOfWeekView.findViewById(R.id.sundayFirst).setVisibility(View.GONE);
            dayOfWeekView.findViewById(R.id.sundayLast).setVisibility(View.VISIBLE);
            for (int i = 1; i < 8; i++) {
                ((CheckBox) dayOfWeekView.getChildAt(i)).setChecked(dayOfWeekList.contains((i % 7) + 1));
            }
        }
    }

    private void getDayOfMonthRule(DayOfMonthRule dateRule) {
        DayOfMonthSelector dayOfMonthSelector = (DayOfMonthSelector) dialogView.findViewById(R.id.dayOfMonth);
        dateRule.setDayOfMonthList(dayOfMonthSelector.getSelectedDayOfMonth());
    }

    private void setDayOfMonthRule(DayOfMonthRule dateRule) {
        List<Integer> dayOfMonthList = dateRule.getDayOfMonthList();
        DayOfMonthSelector dayOfMonthSelector = (DayOfMonthSelector) dialogView.findViewById(R.id.dayOfMonth);
        dayOfMonthSelector.setSelectedDayOfMonth(dayOfMonthList);
    }

    private void getDayOfWeekInMonthRule(DayOfWeekInMonthRule dateRule) {
        Spinner orderInMonthSpinner = (Spinner) dialogView.findViewById(R.id.orderInMonth);
        dateRule.setOrderInMonth(orderInMonthSpinner.getSelectedItemPosition() + 1);
        dateRule.setDayOfWeekList(getDayOfWeekRule());
    }

    private void setDayOfWeekInMonthRule(DayOfWeekInMonthRule dateRule) {
        Spinner orderInMonthSpinner = (Spinner) dialogView.findViewById(R.id.orderInMonth);
        orderInMonthSpinner.setSelection(dateRule.getOrderInMonth() - 1);
        setDayOfWeekRule(dateRule.getDayOfWeekList());
    }

    private void getRevDayOfWeekInMonthRule(RevDayOfWeekInMonthRule dateRule) {
        Spinner orderInMonthSpinner = (Spinner) dialogView.findViewById(R.id.revOrderInMonth);
        dateRule.setRevOrderInMonth(orderInMonthSpinner.getSelectedItemPosition() + 1);
        dateRule.setDayOfWeekList(getDayOfWeekRule());
    }

    private void setRevDayOfWeekInMonthRule(RevDayOfWeekInMonthRule dateRule) {
        Spinner orderInMonthSpinner = (Spinner) dialogView.findViewById(R.id.revOrderInMonth);
        orderInMonthSpinner.setSelection(dateRule.getRevOrderInMonth() - 1);
        setDayOfWeekRule(dateRule.getDayOfWeekList());
    }

    private void getDayMonthRangeRule(DayMonthRangeRule dateRule) {
        DayMonthPicker startDateSpinner = (DayMonthPicker) dialogView.findViewById(R.id.dayMonthRangeStart);
        DayMonthPicker endDateSpinner = (DayMonthPicker) dialogView.findViewById(R.id.dayMonthRangeEnd);

        DayMonth startDayMonth = new DayMonth(
                startDateSpinner.getDay(),
                startDateSpinner.getMonth());
        DayMonth endDayMonth = new DayMonth(
                endDateSpinner.getDay(),
                endDateSpinner.getMonth());

        dateRule.setStartDayMonth(startDayMonth);
        dateRule.setEndDayMonth(endDayMonth);
    }

    private void setDayMonthRangeRule(DayMonthRangeRule dateRule) {
        DayMonth startDayMonth = dateRule.getStartDayMonth();
        DayMonthPicker startDatePicker = (DayMonthPicker) dialogView.findViewById(R.id.dayMonthRangeStart);
        startDatePicker.setDayMonth(startDayMonth.day, startDayMonth.month);

        DayMonth endDayMonth = dateRule.getEndDayMonth();
        DayMonthPicker endDatePicker = (DayMonthPicker) dialogView.findViewById(R.id.dayMonthRangeEnd);
        endDatePicker.setDayMonth(endDayMonth.day, endDayMonth.month);
    }

    private void getDayMonthYearRangeRule(DayMonthYearRangeRule dateRule) {
        DayMonthYearPicker startDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.dayMonthYearRangeStart);
        DayMonthYearPicker endDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.dayMonthYearRangeEnd);

        DayMonthYear startDayMonthYear = new DayMonthYear(
                startDatePicker.getDay(),
                startDatePicker.getMonth(),
                startDatePicker.getYear());
        DayMonthYear endDayMonthYear = new DayMonthYear(
                endDatePicker.getDay(),
                endDatePicker.getMonth(),
                endDatePicker.getYear());

        dateRule.setStartDayMonthYear(startDayMonthYear);
        dateRule.setEndDayMonthYear(endDayMonthYear);
    }

    private void setDayMonthYearRangeRule(DayMonthYearRangeRule dateRule) {
        DayMonthYear startDayMonthYear = dateRule.getStartDayMonthYear();
        DayMonthYearPicker startDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.dayMonthYearRangeStart);
        startDatePicker.setDayMonthYear(startDayMonthYear.day, startDayMonthYear.month, startDayMonthYear.year);

        DayMonthYear endDayMonthYear = dateRule.getEndDayMonthYear();
        DayMonthYearPicker endDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.dayMonthYearRangeEnd);
        endDatePicker.setDayMonthYear(endDayMonthYear.day, endDayMonthYear.month, endDayMonthYear.year);

        startDatePicker.setOnDayMonthYearChangedListener(new DayMonthYearAlign(false, endDatePicker));
        endDatePicker.setOnDayMonthYearChangedListener(new DayMonthYearAlign(true, startDatePicker));
    }

    private void getAlarmXOffYDaysRule(AlarmXOffYDaysRule dateRule) {
        DayMonthYearPicker startDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.alarmXOffYDaysStart);
        DayMonthYear startDayMonthYear = new DayMonthYear(
                startDatePicker.getDay(),
                startDatePicker.getMonth(),
                startDatePicker.getYear());
        dateRule.setStartDayMonthYear(startDayMonthYear);

        EditTextDefault alarmDaysText = (EditTextDefault) dialogView.findViewById(R.id.alarmXOffYDaysAlarm);
        try {
            dateRule.setAlarmDays(Integer.valueOf(alarmDaysText.getTextOrDefault()));
        } catch (NumberFormatException e) {
            // ignore this exception
        }

        EditTextDefault offDaysText = (EditTextDefault) dialogView.findViewById(R.id.alarmXOffYDaysOff);
        try {
            dateRule.setOffDays(Integer.valueOf(offDaysText.getTextOrDefault()));
        } catch (NumberFormatException e) {
            // ignore this exception
        }
    }

    private void setAlarmXOffYDaysRule(AlarmXOffYDaysRule dateRule) {
        DayMonthYear startDayMonthYear = dateRule.getStartDayMonthYear();
        DayMonthYearPicker startDatePicker = (DayMonthYearPicker) dialogView.findViewById(R.id.alarmXOffYDaysStart);
        startDatePicker.setDayMonthYear(startDayMonthYear.day, startDayMonthYear.month, startDayMonthYear.year);

        EditTextDefault alarmDaysText = (EditTextDefault) dialogView.findViewById(R.id.alarmXOffYDaysAlarm);
        alarmDaysText.setText(String.valueOf(dateRule.getAlarmDays()));

        EditTextDefault offDaysText = (EditTextDefault) dialogView.findViewById(R.id.alarmXOffYDaysOff);
        offDaysText.setText(String.valueOf(dateRule.getOffDays()));
    }

    private void getDayMonthYearListRule(DayMonthYearListRule dateRule) {
        DayMonthYearListSelector dayMonthYearListSelector = (DayMonthYearListSelector) dialogView.findViewById(R.id.dayMonthYearList);
        dateRule.setDayMonthYearList(dayMonthYearListSelector.getDayMonthYearList());
    }

    private void setDayMonthYearListRule(DayMonthYearListRule dateRule) {
        DayMonthYearListSelector dayMonthYearListSelector = (DayMonthYearListSelector) dialogView.findViewById(R.id.dayMonthYearList);
        dayMonthYearListSelector.setDayMonthYearList(dateRule.getDayMonthYearList());
    }

    static class DayMonthYearAlign implements OnDayMonthYearChangedListener {
        private boolean great;
        private DayMonthYearPicker thatPicker;
        public DayMonthYearAlign(boolean great, DayMonthYearPicker thatPicker) {
            this.great = great;
            this.thatPicker = thatPicker;
        }
        @Override
        public void onDayMonthYearChanged(DayMonthYearPicker thisPicker) {
            Calendar thisDate = Calendar.getInstance();
            thisDate.set(Calendar.YEAR, thisPicker.getYear());
            thisDate.set(Calendar.MONTH, thisPicker.getMonth());
            thisDate.set(Calendar.DAY_OF_MONTH, thisPicker.getDay());

            Calendar thatDate = (Calendar) thisDate.clone();
            thatDate.set(Calendar.YEAR, thatPicker.getYear());
            thatDate.set(Calendar.MONTH, thatPicker.getMonth());
            thatDate.set(Calendar.DAY_OF_MONTH, thatPicker.getDay());

            if ((great && thisDate.before(thatDate)) || (!great && thisDate.after(thatDate))) {
                thatPicker.setDayMonthYear(thisPicker.getDay(), thisPicker.getMonth(), thisPicker.getYear());
            }
        }
    }

}
