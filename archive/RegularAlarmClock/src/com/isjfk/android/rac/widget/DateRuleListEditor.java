/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.bean.DateRule;
import com.isjfk.android.rac.bean.DateRule.Columns.RuleTypeEnum;
import com.isjfk.android.rac.common.BeanFactory;
import com.isjfk.android.rac.common.RACUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 日期规则编辑控件。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-8-7
 */
public class DateRuleListEditor extends DateRuleListViewer {

    protected int x;
    protected int y;
    protected FloatDateRuleItem floatWindow;

    protected boolean atLeastOne = false;
    protected DateRule reorderRule;

    OnClickListener addDateRuleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            DateRule newDateRule = BeanFactory.newDateRule(RuleTypeEnum.DayMonthYearList);
            addDateRule(newDateRule);
            editRule(newDateRule);
        }
    };

    OnClickListener delDateRuleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (atLeastOne && (getDateRuleCount() <= 1)) {
                RACUtil.popupError(getContext(), R.string.errDateRuleAtLeastOne);
            } else {
                removeDateRule(findParentDateRule(view));
            }
        }
    };

    OnClickListener editEModeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            editEMode(findParentDateRule(view));
        }
    };

    OnClickListener editRuleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            editRule(findParentDateRule(view));
        }
    };

    OnLongClickListener reorderListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            openFloatWindow(findParentDateRuleView(view));
            return true;
        }
    };

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_MOVE) {
                closeFloatWindow();
            }
            return updateTouchPoint((int) event.getRawX(), (int) event.getRawY());
        }
    };

    public DateRuleListEditor(Context context) {
        super(context);
        init(context);
    }

    public DateRuleListEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        floatWindow = new FloatDateRuleItem(this);

        ImageButton addButton = (ImageButton) footer.findViewById(R.id.dateRuleListAddButton);
        addButton.setOnClickListener(addDateRuleListener);
    }

    public boolean isAtLeastOne() {
        return atLeastOne;
    }

    public void setAtLeastOne(boolean atLeastOne) {
        this.atLeastOne = atLeastOne;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.widget.DateRuleListViewer#setDateRuleList(java.util.List)
     */
    @Override
    public void setDateRuleList(List<DateRule> dateRuleList) {
        if (atLeastOne && JavaUtil.isEmpty(dateRuleList)) {
            dateRuleList.add(BeanFactory.newDateRule(RuleTypeEnum.EveryDay));
        }
        super.setDateRuleList(dateRuleList);
    }

    private void editEMode(DateRule dateRule) {
        new EditEModeDialog(this, dateRule).show();
    }

    private void editRule(DateRule dateRule) {
        new EditRuleDialog(this, dateRule).show();
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.widget.DateRuleListViewer#newDateRuleView()
     */
    @Override
    protected View newDateRuleView() {
        return inflater.inflate(R.layout.daterule_edit_list_item, null);
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.widget.DateRuleListViewer#bindDateRuleView(int, android.view.View, com.isjfk.android.rac.bean.DateRule)
     */
    @Override
    protected void bindDateRuleView(int position, View itemView, DateRule dateRule) {
        super.bindDateRuleView(position, itemView, dateRule);

        TextView eModeText = (TextView) itemView.findViewById(R.id.dateRuleEMode);
        TextView descText = (TextView) itemView.findViewById(R.id.dateRuleDesc);

        itemView.findViewById(R.id.dateRuleListDelButton).setOnClickListener(delDateRuleListener);
        eModeText.setOnClickListener(editEModeListener);
        eModeText.setOnLongClickListener(reorderListener);
        eModeText.setOnTouchListener(touchListener);
        descText.setOnClickListener(editRuleListener);
        descText.setOnLongClickListener(reorderListener);
        descText.setOnTouchListener(touchListener);
    }

    /**
     * 弹出排序漂浮窗口。
     *
     * @param view 需要弹出作为漂浮控件的日期规则控件
     */
    protected void openFloatWindow(View view) {
        reorderRule = getViewDateRule(view);
        view.setVisibility(View.INVISIBLE);

        closeFloatWindow();
        RACUtil.requestMotionEvent(this);

        View floatView = newFloatView(posOfDateRule(reorderRule));
        floatWindow.setSize(view.getWidth(), view.getHeight());
        floatWindow.show(floatView, getFloatX(), getFloatY());
    }

    /**
     * 关闭弹出的排序漂浮窗口。
     */
    protected void closeFloatWindow() {
        if (floatWindow.isShowing()) {
            floatWindow.hide();

            getDateRuleView(reorderRule).setVisibility(View.VISIBLE);
            reorderRule = null;

            RACUtil.releaseMotionEvent(this);
        }
    }

    /**
     * 更新用户触摸点的位置并移动浮动窗口。
     *
     * @param x 触摸点X坐标
     * @param y 触摸点Y坐标
     * @return 浮动窗口正在显示返回true，否则返回false
     */
    protected boolean updateTouchPoint(int x, int y) {
        this.x = x;
        this.y = y;
        return moveFloatWindow();
    }

    protected int getFloatX() {
        return x - (floatWindow.getWidth() / 3);
    }

    protected int getFloatY() {
        return y - (floatWindow.getHeight() / 2);
    }

    /**
     * 根据当前触摸点坐标移动浮动窗口。
     *
     * @return 浮动窗口正在显示返回true，否则返回false
     */
    protected boolean moveFloatWindow() {
        boolean isShown = floatWindow.isShowing();
        if (isShown) {
            floatWindow.setPos(getFloatX(), getFloatY());

            int reorderItemPos = posOfDateRule(reorderRule);
            int underItemPos = getItemPosUnderTouchPoint();

            if (shouldReorder(reorderItemPos, underItemPos)) {
                if (reorderItemPos > underItemPos) {
                    while (posOfDateRule(reorderRule) > underItemPos) {
                        DateRule prevDateRule = getDateRule(posOfDateRule(reorderRule) - 1);
                        removeDateRuleNoEvent(prevDateRule);
                        addDateRuleNoEvent(posOfDateRule(reorderRule) + 1, prevDateRule);
                    }
                } else {
                    while (posOfDateRule(reorderRule) < underItemPos) {
                        DateRule nextDateRule = getDateRule(posOfDateRule(reorderRule) + 1);
                        removeDateRuleNoEvent(nextDateRule);
                        addDateRuleNoEvent(posOfDateRule(reorderRule), nextDateRule);
                    }
                }
            }
        }
        return isShown;
    }

    /**
     * 是否应该重新排序日期规则控件。
     * 只有重新排序后，现在触摸点下的控件不会仍然在触摸点下时才重排序。
     *
     * @param reorderItemPos 正在移动的日期规则控件位置
     * @param underItemPos 当前触摸点下的日期规则控件位置
     * @return 需要重排序返回true，否则返回false
     */
    protected boolean shouldReorder(int reorderItemPos, int underItemPos) {
        if ((underItemPos != -1) && (underItemPos != reorderItemPos)) {
            View underItemView = getDateRuleView(underItemPos);
            int[] loc = new int[2];
            underItemView.getLocationOnScreen(loc);
            int underItemViewTop = loc[1];
            int underitemViewBottom = underItemViewTop + underItemView.getHeight();

            if (reorderItemPos > underItemPos) {
                if ((underItemViewTop + floatWindow.getHeight()) > y) {
                    return true;
                }
            } else {
                if ((underitemViewBottom - floatWindow.getHeight()) < y) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前触摸点下的日期规则控件索引。
     *
     * @return 当前触摸点下的日期规则控件索引，如果当前触摸点下没有则返回null
     */
    protected int getItemPosUnderTouchPoint() {
        for (int i = 0; i < getDateRuleCount(); i++) {
            View itemView = getDateRuleView(i);

            int[] loc = new int[2];
            itemView.getLocationOnScreen(loc);

            int itemViewY = loc[1];
            if (itemViewY > y) {
                return -1;
            } else if ((itemViewY + itemView.getHeight()) > y) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onDetachedFromWindow() {
        closeFloatWindow();
        super.onDetachedFromWindow();
    }

    /**
     * 生成指定位置规则的排序漂浮控件。
     *
     * @param position 规则位置
     * @return 指定位置规则的排序漂浮控件
     */
    protected View newFloatView(int position) {
        View itemView = newDateRuleView();
        super.bindDateRuleView(position, itemView, getDateRule(position));
        return itemView;
    }

    /**
     * 查找用户点击的控件对应的日期规则。
     *
     * @param view 用户点击的控件
     * @return 用户点击的控件对应的日期规则
     */
    protected DateRule findParentDateRule(View view) {
        return getViewDateRule(findParentDateRuleView(view));
    }

    /**
     * 查找用户点击的控件所属的日期规则控件。
     *
     * @param view 用户点击的控件
     * @return 日期规则控件，找不到则返回null
     */
    protected View findParentDateRuleView(View view) {
        if (view != null) {
            if (view.getId() == R.id.dateRuleListItem) {
                return view;
            } else if (view.getParent() instanceof View) {
                return findParentDateRuleView((View) view.getParent());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see com.isjfk.android.rac.widget.DateRuleListViewer#setFooter(android.view.LayoutInflater)
     */
    @Override
    protected void setFooter(LayoutInflater inflater) {
        footer = inflater.inflate(R.layout.daterule_edit_list_footer, null);
        addView(footer);

        footerListener = new ShowFooterOnOneTimeDateRuleListener((TextView) footer.findViewById(R.id.dateRuleOneTime));
    }

}
