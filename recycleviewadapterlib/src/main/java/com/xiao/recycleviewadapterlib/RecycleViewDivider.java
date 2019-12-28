package com.xiao.recycleviewadapterlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by xy on 2018/4/18.
 */

public class RecycleViewDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int[] mDividers = null;
    private int mKeepDividerHeight;
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private int mCount = 0;
    private int mMarginLastItem = -1;
    private List<Integer> mPositions;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    public RecycleViewDivider(Context context, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public RecycleViewDivider(Context context, int orientation, int drawableId) {
        this(context, orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
        mKeepDividerHeight = mDividerHeight;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public RecycleViewDivider(Context context, int orientation, int dividerHeight,
                              int dividerColor) {
        this(context, orientation);
        mDividerHeight = dividerHeight;
        mKeepDividerHeight = mDividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public RecycleViewDivider(Context context, int orientation, int dividerHeight,
                              int marginLastItem, List<Integer> position, int dividerColor) {
        this(context, orientation);
        mCount = 0;
        mDividerHeight = dividerHeight;
        mKeepDividerHeight = mDividerHeight;
        mMarginLastItem = marginLastItem;
        mPositions = position;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }


    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int totalCount = parent.getAdapter().getItemCount();
        if (mMarginLastItem != -1) {
            if (mDividers == null) {
                mDividers = new int[totalCount];
            }
            if (-1 != mPositions.indexOf(mCount)) {
                mDividerHeight = mMarginLastItem;
            } else {
                mDividerHeight = mKeepDividerHeight;
            }
            mCount++;
            mDividers[mCount - 1] = mDividerHeight;
            if (mCount < totalCount) {
                outRect.set(0, 0, 0, mDividerHeight);
            }
        } else {
            if (mCount < totalCount - 1) {
                outRect.set(0, 0, 0, mDividerHeight);
                mCount++;
            }
        }

    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        if (childSize == 0) {
            return;
        }
        for (int i = 0; i < childSize - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom;
            if (mDividers != null) {
                bottom = top + mDividers[i];
            } else {
                bottom = top + mDividerHeight;
            }
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制纵向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    public void clearCount() {
        mCount = 0;
    }
}
