package com.sonuan.xxxxsimples.other;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.sonuan.xxxxsimples.adpter.LiveFootballPenaltykickAdapter;

import java.util.Arrays;


/**
 * @author wusongyuan
 * @date 2017.07.24
 * @desc 直播-球王争霸-点球-老虎机布局
 */

public class LiveOneArmBanditLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "LiveOneArmBanditLayoutM";
    private final LinearInterpolator mInterpolator;
    private int mCount;

    private int mHorizoallySpanCount;
    private int mVerticalSpanCount;
    private int mFromPosition;
    private int mToPosition;
    private RecyclerView mRecyclerView;
    private ValueAnimator mObjectAnimator;

    public LiveOneArmBanditLayoutManager(Context context, int horizoallySpanCount, int verticalSpanCount) {
        super();
        setHorizoallySpanCount(horizoallySpanCount);
        setVerticalSpanCount(verticalSpanCount);
        mInterpolator = new LinearInterpolator();
    }

    public void setHorizoallySpanCount(int spanCount) {
        mHorizoallySpanCount = spanCount;
    }

    public void setVerticalSpanCount(int verticalSpanCount) {
        mVerticalSpanCount = verticalSpanCount;

    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mObjectAnimator != null && mObjectAnimator.isStarted()) {
            return;
        }
        //super.onLayoutChildren(recycler, state);
        if (getItemCount() == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);
        //初始化时调用 填充childView
        fill(recycler, state, 0);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int topOffset = getPaddingTop();//布局时的上偏移
        int leftOffset = getPaddingLeft();//布局时的左偏移
        int rightTop = (mVerticalSpanCount + mHorizoallySpanCount - 1);
        int rightBottom = (2 * mVerticalSpanCount + mHorizoallySpanCount - 2);
        int maxCount = (2 * mVerticalSpanCount + 2 * mHorizoallySpanCount - 4);
        int parentWidth = getWidth();
        Log.i(TAG, "fill: " + parentWidth);
        int width = parentWidth / mHorizoallySpanCount;
        int height = width;
        //顺序addChildView
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int width1 = getDecoratedMeasuredWidth(child);
            int height1 = getDecoratedMeasuredHeight(child);
            Log.i(TAG, "fill: "+ i + " " + width1 + " " + height1 + " " + getLeftDecorationWidth(child) + " " + getRightDecorationWidth(child));
            int horItemDecoration = getRightDecorationWidth(child) - getLeftDecorationWidth(child);
            int verItemDecoration = getBottomDecorationHeight(child) - getTopDecorationHeight(child);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            layoutParams.width = width - horItemDecoration;
            layoutParams.height = height - verItemDecoration;
            child.setLayoutParams(layoutParams);
            if (i < mVerticalSpanCount && i < mHorizoallySpanCount) {
                topOffset = (mVerticalSpanCount - i - 1) * height;
                leftOffset = 0;
            } else if (i >= mVerticalSpanCount && i < rightTop) {
                topOffset = 0;
                leftOffset = (i - mVerticalSpanCount + 1) * width;
            } else if (i >= rightTop && i < rightBottom) {
                topOffset = (i - rightTop + 1) * height;
                leftOffset = (mHorizoallySpanCount - 1) * width;
            } else if (i >= rightBottom && i < maxCount) {
                topOffset = (mVerticalSpanCount - 1) * height;
                leftOffset = (maxCount - i) * width;
                Log.i(TAG, "fill: "+ i + " " + topOffset + " " + leftOffset + " | " + width + " " + height);
            }

            //Log.i(TAG, "fill: " + layoutParams.width);

            layoutDecorated(child, leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public void startAnims(int endIndex) {
        if (mRecyclerView == null) {
            throw new NullPointerException("mRecyclerView is null");
        }
        int[] values = null;
        mCount = getItemCount();
        if (mCount == 0) {
            throw new IllegalArgumentException("getItemCount() is 0.");
        }
        if (mCurrRunPosition < endIndex ) {
            if ((endIndex - mCurrRunPosition) % mCount > 3) {
                values = new int[]{mCurrRunPosition, endIndex};
            } else {
                values = new int[]{mCurrRunPosition, endIndex, endIndex + mCount};
            }
        } else if (mCurrRunPosition > endIndex) {
            if ((mCount + endIndex - mCurrRunPosition) % mCount > 3) {
                values = new int[]{
                        mCurrRunPosition, mCount, mCount + endIndex
                };
            } else {
                values = new int[]{
                        mCurrRunPosition, mCount + endIndex, mCount + endIndex + mCount
                };
            }

        } else { // ==
            values = new int[]{
                    mCurrRunPosition, mCurrRunPosition + mCount
            };
        }
        mFromPosition = values[0];
        mToPosition = values[values.length - 1];
        Log.i(TAG, "run: " + mCurrRunPosition + "->"+ endIndex +" "+ Arrays.toString(values));
        int duration = (values[values.length - 1] - values[0]) * 50;
        mObjectAnimator = mObjectAnimator.ofInt(values)
                .setDuration(duration);
        mObjectAnimator.setInterpolator(mInterpolator);
        mObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrViewHolder != null) {
                    Animator animator = mCurrViewHolder.animHighLight();
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mAnimatorListener != null) {
                                mAnimatorListener.onAnimationEnd(null);
                            }
                        }
                    });
                    animator.start();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (mAnimatorListener != null) {
                    mAnimatorListener.onAnimationStart(null);
                }
            }
        });
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int value;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (int) animation.getAnimatedValue();
                setCurrRunPosition(value);
            }
        });
        mObjectAnimator.start();
    }

    private LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder mCurrViewHolder;
    private LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder mLastOneViewHolder = null;
    private LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder mLastTwoViewHolder = null;
    private LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder mLastThreeViewHolder = null;
    private int mCurrRunPosition;
    private void setCurrRunPosition(int runPosition) {
        mCurrRunPosition = runPosition % mCount;
        if (mCount > mCurrRunPosition) {
            if (mCurrViewHolder != null) {
                mCurrViewHolder.reset();
            }
            if (mLastOneViewHolder != null) {
                mLastOneViewHolder.reset();
            }
            if (mLastTwoViewHolder != null) {
                mLastTwoViewHolder.reset();
            }
            if (mLastThreeViewHolder != null) {
                mLastThreeViewHolder.reset();
            }
            mCurrViewHolder = null;
            mLastOneViewHolder = null;
            mLastTwoViewHolder = null;
            mLastThreeViewHolder = null;
            mCurrViewHolder = (LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mCurrRunPosition);

            if (mToPosition - runPosition >= 1 && runPosition - mFromPosition >= 1) {
                int index1 = (mCurrRunPosition + mCount - 1) % mCount;
                mLastOneViewHolder = (LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder) mRecyclerView.findViewHolderForAdapterPosition(index1);
            }
            if (mToPosition - runPosition >= 2 && runPosition - mFromPosition >= 2) {
                int index2 = (mCurrRunPosition + mCount - 2) % mCount;
                mLastTwoViewHolder = (LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder) mRecyclerView.findViewHolderForAdapterPosition(index2);
            }
            if (mToPosition - runPosition >= 3 && runPosition - mFromPosition >= 3) {
                int index3 = (mCurrRunPosition + mCount - 3) % mCount;
                mLastThreeViewHolder = (LiveFootballPenaltykickAdapter.FootballPenaltykickViewHolder) mRecyclerView.findViewHolderForAdapterPosition(index3);
            }
            //Log.i(TAG, "setCurrRunPosition: " + mFromPosition + " " + runPosition + " " + mToPosition);

            mCurrViewHolder.setHighLight();
            if (mLastOneViewHolder != null) {
                //Log.i(TAG, "setCurrRunPosition: one");
                mLastOneViewHolder.setHighLight();
            }
            if (mLastTwoViewHolder != null) {
                //Log.i(TAG, "setCurrRunPosition: two");
                mLastTwoViewHolder.setHighLight();
            }
            if (mLastThreeViewHolder != null) {
                //Log.i(TAG, "setCurrRunPosition: three");
                mLastThreeViewHolder.setHighLight();
            }
        }
    }

    public AnimatorListenerAdapter mAnimatorListener;

    public void setAnimatorListener(AnimatorListenerAdapter animatorListener) {
        mAnimatorListener = animatorListener;
    }
}
