package com.example.doublerecyclerview.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.example.doublerecyclerview.R;
import com.example.doublerecyclerview.adapter.SampleAdapter;
import com.example.doublerecyclerview.listener.ExpandListener;
import com.example.doublerecyclerview.util.ScreenUtil;
import com.example.doublerecyclerview.view.TabViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者:Created by sinbara on 2018/9/19.
 * 邮箱:hrb940258169@163.com
 */

public class SubFragment extends Fragment implements ExpandListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private VirtualLayoutManager layoutManager;
    private DelegateAdapter delegateAdapter;

    private boolean isExpand=true;
    private GridLayoutHelper staggerLayoutHelper;
    private TabViewPager.Listener listener;
    private int yvel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub, container, false);
        ButterKnife.bind(this, view);
        layoutManager =new VirtualLayoutManager(getActivity());
        delegateAdapter=new DelegateAdapter(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        staggerLayoutHelper = new GridLayoutHelper(2);
        staggerLayoutHelper.setPaddingLeft(dip2px(16));
        staggerLayoutHelper.setPaddingRight(dip2px(16));
        staggerLayoutHelper.setHGap(dip2px(16));
        staggerLayoutHelper.setPaddingTop(dip2px(16));
        staggerLayoutHelper.setBgColor(getActivity().getResources().getColor(R.color.color_white));
        SampleAdapter sampleAdapter=new SampleAdapter(getActivity(),staggerLayoutHelper,getItemWidth());
        delegateAdapter.addAdapter(sampleAdapter);
        recyclerView.setAdapter(delegateAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                isExpand=layoutManager.getOffsetToStart()==-dip2px(16);
                if (isExpand){
                    if (listener!=null){
                        listener.isScrollTop(true,yvel);
                    }
                }else {
                    if (listener!=null){
                        listener.isScrollTop(false,yvel);
                    }
                }
                //Log.e("han","offerStart=="+layoutManager.getOffsetToStart());
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                yvel=velocityY;
                if (yvel<0){
                    Log.e("han","开始速度=="+yvel);
                    float t= getOffer()*1000/yvel;
                    yvel=yvel-(int)(12*t/0.9);
                    Log.e("han","結束速度=="+yvel);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean isExpand() {
        return layoutManager.getOffsetToStart()==-dip2px(16);
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    @Override
    public int getOffer() {
        return layoutManager.getOffsetToStart()- ScreenUtil.dip2px(16);
    }

    private int dip2px(int dipValue) {
        DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        float scale = dm.density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int getItemWidth(){
        DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        return (dm.widthPixels - dip2px(48)) / 2;
    }

    public SubFragment setListener(TabViewPager.Listener listener){
        this.listener=listener;
        return this;
    }
}
