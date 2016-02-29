/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * 日期规则浮动部件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-10
 */
public class FloatDateRuleItem {

    private WindowManager wm;
    private LayoutParams lp;

    private boolean showing = false;
    private View parentView = null;
    private View contentView = null;

    public FloatDateRuleItem (View view) {
        this.parentView = view;
        wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        lp = new LayoutParams();
        lp.packageName = view.getContext().getPackageName();
        lp.type = LayoutParams.TYPE_APPLICATION_PANEL;
        lp.flags = lp.flags
                | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_NOT_TOUCHABLE;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.gravity = Gravity.TOP | Gravity.LEFT;
    }

    public void show(View contentView, int x, int y) {
        if (!showing) {
            this.contentView = contentView;
            lp.token = parentView.getWindowToken();
            lp.x = x;
            lp.y = y;
            wm.addView(contentView, lp);
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            wm.removeView(contentView);
            showing = false;
        }
    }

    public void setPos(int x, int y) {
        lp.x = x;
        lp.y = y;
        update();
    }

    public void setSize(int width, int height) {
        if (width != -1) {
            lp.width = width;
        }
        if (height != -1) {
            lp.height = height;
        }
        update();
    }

    public int getX() {
        return lp.x;
    }

    public int getY() {
        return lp.y;
    }

    public int getWidth() {
        return lp.width;
    }

    public int getHeight() {
        return lp.height;
    }

    public View getView() {
        return contentView;
    }

    public boolean isShowing() {
        return showing;
    }

    protected void update() {
        if (showing) {
            wm.updateViewLayout(contentView, lp);
        }
    }

}
