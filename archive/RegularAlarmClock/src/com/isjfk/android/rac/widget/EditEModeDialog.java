/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 日期规则生效模式修改对话框。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-14
 */
public class EditEModeDialog implements OnClickListener {

    private DateRuleListEditor editor;
    private DateRule dateRule;
    private AlertDialog dialog;

    public EditEModeDialog(DateRuleListEditor editor, DateRule dateRule) {
        this.editor = editor;
        this.dateRule = dateRule;

        AlertDialog.Builder builder = new AlertDialog.Builder(editor.getContext());
        builder.setTitle(R.string.dialogEditEModeTitle);
        builder.setSingleChoiceItems(R.array.dateRuleEMode, dateRule.getEMode(), this);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int itemIndex) {
        dateRule.setEMode(itemIndex);
        editor.resetDateRuleView(dateRule);
        dialog.dismiss();
    }

}
