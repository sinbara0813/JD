package com.example.doublerecyclerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 作者:Created by sinbara on 2019/3/27.
 * 邮箱:hrb940258169@163.com
 */

public class TabRecyclerView extends RecyclerView {
    private String TAG=getClass().getSimpleName();
    private int mScrollPointerId = -1;
    private VelocityTracker mVelocityTracker;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mLastTouchY;
    private int mTouchSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private final int[] mNestedOffsets = new int[2];
    private boolean isUp;
    private boolean isDown;
    private float yvel;
    private boolean isEnterFrist;

    public TabRecyclerView(Context context) {
        this(context, null);
    }

    public TabRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        TabViewPager viewPager=getViewPager();
        boolean intercept=viewPager!=null?!viewPager.isTop():false;
        Log.e(TAG,"onInterceptTouchEvent=="+intercept);
        return super.onInterceptTouchEvent(e)||intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        TabViewPager viewPager=null;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        boolean eventAddedToVelocityTracker = false;

        final MotionEvent vtev = MotionEvent.obtain(e);
        final int action = e.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsets[0] = mNestedOffsets[1] = 0;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsets[0] = mNestedOffsets[1] = 0;
        }
        vtev.offsetLocation(mNestedOffsets[0], mNestedOffsets[1]);
        vtev.offsetLocation(mNestedOffsets[0], mNestedOffsets[1]);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = e.getPointerId(0);
                mInitialTouchY = mLastTouchY = (int) (e.getY() + 0.5f);
                break;
            case MotionEvent.ACTION_MOVE:
                final int index = e.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e("TabRecyclerView", "Error processing scroll; pointer index for id "
                            + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }

                final int y = (int) (e.getY(index) + 0.5f);
                int dy = mLastTouchY - y;
                if (dy > 0) {
                    isUp = true;
                    isDown=false;
                } else if (dy < 0) {
                    isDown = true;
                    isUp=false;
                }
                mLastTouchY = y;
                viewPager=getViewPager();
                if (viewPager!=null&&viewPager.isTop()){
                    if (isUp||(!viewPager.isExpand()&&isDown)){
                        isEnterFrist=true;
                        viewPager.handleEvent(e);
                        return true;
                    }
                    if (viewPager.isExpand()&&isDown){
                        if (isEnterFrist){
                            e.setAction(MotionEvent.ACTION_DOWN);
                            isEnterFrist=false;
                            viewPager.resetEvent();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                viewPager = getViewPager();
                if (viewPager!=null&&viewPager.isTop()){
                    if (isUp||(!viewPager.isExpand()&&isDown)){
                        isEnterFrist=true;
                        viewPager.handleEvent(e);
                        return true;
                    }
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                isUp=false;
                isDown=false;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                break;
        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        Log.e(TAG,"handleEvent--"+(e.getAction()==MotionEvent.ACTION_DOWN?"DOWN":e.getAction()==MotionEvent.ACTION_MOVE?"MOVE":"UP")+"--"+"x=="+e.getX()+",y=="+e.getY());
        if (e.getAction()==MotionEvent.ACTION_DOWN){
            Log.d(TAG,"x=="+e.getX()+",y=="+e.getY());
        }
        return super.onTouchEvent(e);
    }

    private TabViewPager getViewPager() {
        int size = getChildCount();
        if (size > 0) {
            View view = getChildAt(size - 1);
            if (view instanceof TabViewPager) {
                return (TabViewPager) view;
            }
        }
        return null;
    }
}
