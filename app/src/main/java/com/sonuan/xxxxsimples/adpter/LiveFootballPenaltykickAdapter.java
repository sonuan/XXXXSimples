package com.sonuan.xxxxsimples.adpter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sonuan.xxxxsimples.R;

import java.util.List;

/**
 * @author wusongyuan
 * @date 2017.07.24
 * @desc
 */

public class LiveFootballPenaltykickAdapter
        extends RecyclerView.Adapter<LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder> {

    private static final String TAG = "LiveFootballPenaltykick";
    private List<String> mDatas;
    private int mSelectedPosition = -1;

    public void setDatas(List<String> datas) {
        mDatas = datas;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;

        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    @Override
    public FootballPenaltykickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.live_football_penaltykick_item, parent, false);
        //RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        //layoutParams.width = 100;
        //layoutParams.height = 100;
        //view.setLayoutParams(layoutParams);
        return new FootballPenaltykickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FootballPenaltykickViewHolder holder, int position) {
        holder.setData(mDatas.get(position), mSelectedPosition);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public static class FootballPenaltykickViewHolder extends RecyclerView.ViewHolder {
        View mParentView;
        TextView mTvPenaltykick;
        ImageView mHighLightIv;

        public FootballPenaltykickViewHolder(View itemView) {
            super(itemView);
            mParentView = itemView.findViewById(R.id.live_football_penaltykick_rl);
            mTvPenaltykick = (TextView) itemView.findViewById(R.id.live_football_penaltykick_tv);
            mHighLightIv = (ImageView) itemView.findViewById(R.id.live_football_penaltykick_iv);
        }

        public void setData(String text, int selectedPosition) {
            mTvPenaltykick.setText(text);
            //if (selectedPosition == getAdapterPosition()) {
            //    mParentView.setBackgroundColor(0x33000000);
            //} else {
            //    mParentView.setBackgroundColor(0x99999999);
            //}
            mTvPenaltykick.setBackgroundColor(0x343412d3);
        }

        public void reset() {
            //itemView.setBackgroundResource(R.mipmap.live_rank_football_penaltykick_normal_ic);
            mHighLightIv.setBackgroundColor(0x00000000);
        }

        public void setHighLight() {
            //itemView.setBackgroundResource(R.mipmap.live_rank_football_penaltykick_selected_ic);
            mHighLightIv.setBackgroundResource(R.mipmap.live_rank_football_penaltykick_selected_ic);
            //mHighLightIv.setBackgroundColor(0x33000000);
        }

        public Animator animHighLight() {
            ValueAnimator alphaAnim = ValueAnimator.ofFloat(0, 1)
                    .setDuration(200);
            alphaAnim.setRepeatCount(3);
            alphaAnim.setRepeatMode(ValueAnimator.REVERSE);
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mHighLightIv.setAlpha(value);
                }
            });
            return alphaAnim;
        }
    }
}
