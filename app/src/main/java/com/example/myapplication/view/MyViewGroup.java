package com.example.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * created by lvchao 2019-12-13
 * describe:
 */
public class MyViewGroup extends LinearLayout {
    private static final String TAG = "MyViewGroup";

    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e(TAG, "MyViewGroup dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            intercept = false;//Down事件不能拦截否则childView将接收不到任何事件
        } else {
            intercept = true;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG, "MyViewGroup Touch down");
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "MyViewGroup Touch move");
            }
            break;
            case MotionEvent.ACTION_UP: {
                Log.e(TAG, "MyViewGroup Touch up");
            }
            break;
        }
        return true;
    }
}
