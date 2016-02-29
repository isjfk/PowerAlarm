/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import com.isjfk.android.util.JavaUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 带有默认值的EditText。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-7
 */
public class EditTextDefault extends EditText {

    public EditTextDefault(Context context) {
        super(context);
    }

    public EditTextDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextDefault(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getTextOrDefault() {
        String text = getText().toString();
        if (JavaUtil.isEmpty(text)) {
            text = getHint().toString();
        }
        return text;
    }

}
