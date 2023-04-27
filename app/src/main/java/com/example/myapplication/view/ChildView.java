package com.example.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * created by lvchao 2019-12-13
 * describe:
 */
public class ChildView extends ListView {
    private static final String TAG = "ChildView";
    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    public ChildView(Context context) {
        super(context);
    }

    public ChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG, "ChildView down");
//                getParent().requestDisallowInterceptTouchEvent(false);
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int dy = y - mLastY;
                int dx = x - mLastX;
//                if (isReachTop() && y > mLastY) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
                Log.e(TAG, "ChildView move" + (Math.abs(dx) > 0));
            }
            break;
        }
        mLastY = y;
        mLastX = x;

        return super.dispatchTouchEvent(event);
    }

    private boolean isReachTop() {
        boolean result = false;
        if (getFirstVisiblePosition() == 0) {
            final View topChildView = getChildAt(0);
            result = topChildView.getTop() == 0;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touce = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG, "ChildView onTouchEvent down");
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "ChildView onTouchEvent move");
            }
            break;
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "ChildView onTouchEvent up");
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
                Log.e(TAG, "ChildView onTouchEvent ACTION_CANCEL");
            }
            break;
        }

        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
