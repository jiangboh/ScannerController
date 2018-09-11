package com.bravo.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Jack.liao on 2016/9/20.
 */

public class UserDefineListView extends ListView {
    private int mScreenWidth;		//屏幕宽度
    private int mDownX;				//按下点的x值
    private int mDownY;				//按下点的y值
    private int mDeleteBtnWidth;	//删除按钮的宽度
    private boolean isDeleteShown;	//删除按钮是否正在显示
    private int iState;
    private final int STATE_NORMAL = 1;
    private final int STATE_RECOVER = 2;
    private final int STATE_MOVE = 3;

    private ViewGroup mPointChild;	//当前处理的item
    private LinearLayout.LayoutParams mLayoutParams;	//当前处理的item的LayoutParams

    public UserDefineListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserDefineListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return performActionDown(ev);
            case MotionEvent.ACTION_MOVE:
                return performActionMove(ev);
            case MotionEvent.ACTION_UP:
                return performActionUp(ev);
        }
        return super.onTouchEvent(ev);
    }

    //处理action_down事件
    private boolean performActionDown(MotionEvent ev) {
        if(isDeleteShown) {
            turnToNormal();
            iState = STATE_RECOVER;
            return true;
        }
        if (iState == STATE_MOVE) {
            iState = STATE_NORMAL;
        }
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();

        //获取当前点的item
        mPointChild = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY)
                - getFirstVisiblePosition());
        if (mPointChild != null) {
            //获取删除按钮的宽度
            mDeleteBtnWidth = mPointChild.getChildAt(1).getLayoutParams().width;
            mLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0)
                    .getLayoutParams();
            // 为什么要重新设置layout_width 等于屏幕宽度
            // 因为match_parent时，不管你怎么滑，都不会显示删除按钮
            // why？ 因为match_parent时，ViewGroup就不去布局剩下的view
            mLayoutParams.width = mScreenWidth;
            mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
        }
        return super.onTouchEvent(ev);
    }

    private boolean performActionMove(MotionEvent ev) {
        if (iState == STATE_RECOVER) {
            return true;
        }
        if (mPointChild != null) {
            int nowX = (int) ev.getX();
            int nowY = (int) ev.getY();
            if (Math.abs(nowX - mDownX) > Math.abs(nowY - mDownY)) {
                //如果向左滑动
                if (nowX < mDownX) {
                    //计算要偏移的距离
                    int scroll = (nowX - mDownX) / 2;
                    if (-scroll >= 10)  {
                        if (iState != STATE_MOVE) {
                        }
                        iState = STATE_MOVE; }
                    //如果大于了删除按钮的宽度， 则最大为删除按钮的宽度
                    if (-scroll >= mDeleteBtnWidth) {
                        scroll = -mDeleteBtnWidth;
                    }
                    //重新设置leftMargin
                    mLayoutParams.leftMargin = scroll;
                    mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
                }

                return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    //处理action_up事件
    private boolean performActionUp(MotionEvent ev) {
        if (iState == STATE_RECOVER) {
            iState = STATE_NORMAL;
            return true;
        }
        //偏移量大于button的一半，则显示button
        //否则恢复默认
        if (mPointChild == null) {
            return super.onTouchEvent(ev);
        }
        if(-mLayoutParams.leftMargin >= mDeleteBtnWidth / 2) {
            mLayoutParams.leftMargin = -mDeleteBtnWidth;
            isDeleteShown = true;
        }else {
            turnToNormal();
        }

        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
        return super.onTouchEvent(ev);
    }

    //变为正常状态
    public void turnToNormal() {
        if (mPointChild != null) {
            mLayoutParams.leftMargin = 0;
            mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
            isDeleteShown = false;
        }
    }

    /*
     * 当前是否可点击
     * @return 是否可点击
    */
    public boolean canClick() {
        if (iState == STATE_MOVE) {
            return false;
        }
        return !isDeleteShown;
    }
}
