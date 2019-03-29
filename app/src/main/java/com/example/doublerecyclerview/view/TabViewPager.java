package com.example.doublerecyclerview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.doublerecyclerview.R;
import com.example.doublerecyclerview.adapter.TabPagerAdapter;
import com.example.doublerecyclerview.fragment.SubFragment;
import com.example.doublerecyclerview.listener.ExpandListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者:Created by sinbara on 2018/9/19.
 * 邮箱:hrb940258169@163.com
 */

public class TabViewPager extends FrameLayout{

    @Bind(R.id.sliding_tab)
    SlidingTabLayout slidingTab;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private Context mContext;
    private TabPagerAdapter tabAdapter;

    private TabRecyclerView outRecyclerView; //外部recyclerview
    private View currentScrollView; //内部recyclerview
    private boolean isEnterFirst =true; //事件由外部处理变成内部处理的临界点

    public TabViewPager(@NonNull Context context, TabRecyclerView outRecyclerView) {
        super(context);
        this.outRecyclerView=outRecyclerView;
        init(context);
    }

    public TabViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.mContext=context;
        LayoutInflater.from(context).inflate(R.layout.layout_tab_view_pager, this, true);
        ButterKnife.bind(this,this);
        viewPager.getLayoutParams().height=getDisplayHeight()-dip2px(112)-getStatusBarHeight(context); //设置viewpager高度 112viewpager滑动到顶部时到屏幕顶部的高度
    }

    public void setTabData() {
        int size = 10;
        final List<Fragment> fragments = new ArrayList<>();
        List<String> cateList=new ArrayList<>();
        for (int i = 0; i < size; i++) {
            fragments.add(new SubFragment().setListener(new Listener() {
                @Override
                public void isScrollTop(boolean is) {
                    if (is){
                        outRecyclerView.startScroll();
                    }
                }
            }));
            switch (i){
                case 0:
                    cateList.add("精选推荐");
                    break;
                case 1:
                    cateList.add("平价手机");
                    break;
                case 2:
                    cateList.add("电子书童");
                    break;
                case 3:
                    cateList.add("军迷吧");
                    break;
                case 4:
                    cateList.add("搞机");
                    break;
                case 5:
                    cateList.add("个性潮装");
                    break;
                case 6:
                    cateList.add("型男");
                    break;
                case 7:
                    cateList.add("户外服饰");
                    break;
                case 8:
                    cateList.add("风雨无阻");
                    break;
                case 9:
                    cateList.add("酷跑一族");
                    break;
            }
        }
        tabAdapter = new TabPagerAdapter(((FragmentActivity)mContext).getSupportFragmentManager(), fragments, cateList);
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentScrollView=((ExpandListener)fragments.get(position)).getScrollableView();//获取当前内部滑动的recyclerview
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slidingTab.setViewPager(viewPager);
        slidingTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                RecyclerView.SmoothScroller scroller=new LinearSmoothScroller(mContext){
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 100f / displayMetrics.densityDpi; //这里控制滑动的速度
                    }
                };
                scroller.setTargetPosition(4);
                outRecyclerView.getLayoutManager().startSmoothScroll(scroller);//让tab已一定的速度滑动到顶部
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    public boolean isTop() {
        return getTop() == dip2px(56);
    }

    public void handleEvent(MotionEvent event){
        if (isEnterFirst){
            event.setAction(MotionEvent.ACTION_DOWN);
            isEnterFirst =false;
        }
        Log.e("han","handleEvent--"+(event.getAction()==MotionEvent.ACTION_DOWN?"DOWN":event.getAction()==MotionEvent.ACTION_MOVE?"MOVE":"UP")+"--"+"x=="+event.getX()+",y=="+event.getY());
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            Log.d("han","x=="+event.getX()+",y=="+event.getY());
        }
        getScrollView().onTouchEvent(event);
    }

    public void resetEvent(){
        isEnterFirst=true;
    }

    public boolean isExpand(){
        boolean is=false;
        TabPagerAdapter mainPageAdapter= (TabPagerAdapter) viewPager.getAdapter();
        Fragment fragment= null;
        if (mainPageAdapter!=null){
            fragment=mainPageAdapter.getItem(viewPager.getCurrentItem());
        }
        if (fragment!=null&&fragment instanceof ExpandListener){
            is=((ExpandListener)fragment).isExpand();
        }
        return is;
    }

    public int getOffer(){
        TabPagerAdapter mainPageAdapter= (TabPagerAdapter) viewPager.getAdapter();
        Fragment fragment= null;
        if (mainPageAdapter!=null){
            fragment=mainPageAdapter.getItem(viewPager.getCurrentItem());
        }
        if (fragment!=null&&fragment instanceof ExpandListener){
            return ((ExpandListener)fragment).getOffer();
        }
        return 0;
    }

    private View getScrollView(){
        if (currentScrollView==null){
            return ((ExpandListener)tabAdapter.getItem(viewPager.getCurrentItem())).getScrollableView();
        }
        return currentScrollView;
    }

    private int dip2px(int dipValue){
        DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
        float scale=dm.density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int getDisplayHeight(){
        DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return sbar;
    }

    public interface Listener{
        public void isScrollTop(boolean is);
    }
}
