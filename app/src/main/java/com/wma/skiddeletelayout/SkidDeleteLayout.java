package com.wma.skiddeletelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SkidDeleteLayout extends LinearLayout {

    Scroller mScroller;
    Context mContext;

    public SkidDeleteLayout(Context context) {
        this(context, null);
    }

    public SkidDeleteLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkidDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScroller = new Scroller(mContext);
        setOrientation(HORIZONTAL);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    float firstX = 0, firstY = 0;
    int distance = 0;
    boolean isShow = false;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = event.getX();
                firstY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((firstY = event.getY()) != 0 && Math.abs((firstX - event.getX())) > 10) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (!isShow) {
                    distance = (int) (firstX - event.getX());
                    if (distance > hideChildWidth) {
                        distance = hideChildWidth;
                    }
                    if (distance < 0) {
                        distance = 0;
                    }
                } else {
                    distance = hideChildWidth + (int) (firstX - event.getX());
                    if (distance < 0) {
                        distance = 0;
                    }
                    if (distance > hideChildWidth) {
                        distance = hideChildWidth;
                    }
                }
                scrollTo(distance, 0);
                break;
            case MotionEvent.ACTION_UP:
                if (distance == hideChildWidth) { //展示
                    isShow = true;
                } else if (distance == 0) {//隐藏
                    isShow = false;
                }
                if (isShow) {
                    if ((hideChildWidth - distance) >= hideChildWidth / 6) {//去展示
                        mScroller.startScroll(distance, 0, -distance, 0);
                        isShow = false;
                        invalidate();
                    } else {//去隐藏
                        mScroller.startScroll(distance, 0, hideChildWidth - distance, 0);
                        isShow = true;
                        invalidate();
                    }
                } else {
                    if (distance >= hideChildWidth / 6) {//去展示
                        mScroller.startScroll(distance, 0, hideChildWidth - distance, 0);
                        isShow = true;
                        invalidate();
                    } else {//去隐藏
                        mScroller.startScroll(distance, 0, -distance, 0);
                        isShow = false;
                        invalidate();
                    }
                }
                if (onShowListener != null) {
                    onShowListener.onShow(isShow);
                }
                break;
        }
        return true;
    }

    private int hideChildWidth = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            if (i == 1) {
                hideChildWidth = measuredWidth;
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY()); // 调用Scroller中存储的值
            postInvalidate();
        }
    }


    public boolean getIsShow() {
        return isShow;
    }


    public void addOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public OnShowListener onShowListener;

    public interface OnShowListener {
        void onShow(boolean isShow);
    }

    public void hideOrShow(boolean show) {
        if (!show) {
            scrollTo(0, 0);
        } else {
            scrollTo(hideChildWidth, 0);
        }
        isShow = show;
    }
}
