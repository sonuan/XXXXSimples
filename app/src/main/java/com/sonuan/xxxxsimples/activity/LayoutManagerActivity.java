package com.sonuan.xxxxsimples.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sonuan.xxxxsimples.R;
import com.sonuan.xxxxsimples.adpter.LiveFootballPenaltykickAdapter;
import com.sonuan.xxxxsimples.base.BaseActivity;
import com.sonuan.xxxxsimples.other.LiveOneArmBanditLayoutManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LayoutManagerActivity extends BaseActivity {
    private static final String TAG = "LayoutManagerActivity";

    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private RecyclerView.ViewHolder mPrev33;
    private LinearInterpolator mInterpolator;
    private Random mRandom;
    private int mToPosition;
    private int mFromPosition;
    private int mCount;
    private LiveOneArmBanditLayoutManager mLayoutManager;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_layout_manager);

    }

    @Override
    protected void initDatas(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) findViewById(R.id.live_football_penaltykick_recyclerview);
        mLayoutManager = new LiveOneArmBanditLayoutManager(this, 6, 3);
        mLayoutManager.setRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this).size(10).color(android.R.color.transparent).showLastDivider().build());
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).size(20).color(android.R.color.transparent).showLastDivider().build());
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        final LiveFootballPenaltykickAdapter adapter = new LiveFootballPenaltykickAdapter();
        mRecyclerView.setAdapter(adapter);
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            datas.add(i, "屌" + i);
        }
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();

        mHandler = new Handler();

        mInterpolator = new LinearInterpolator();
        mRandom = new Random();
        mCount = adapter.getItemCount();
    }


    public void start(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLayoutManager != null) {
                    mLayoutManager.startAnims(mRandom.nextInt(mCount - 1));
                }
            }
        }, 1000);
    }

}
