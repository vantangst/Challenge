package com.co.challengeliv3ly.widgets.loading;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RotateLoadingView extends View {

    private static final int DEFAULT_WIDTH = 6;
    private static final int DEFAULT_SHADOW_POSITION = 2;
    private static final int DEFAULT_SPEED_OF_DEGREE = 10;
    private static final int DEFAULT_SHADOW_COLOR = Color.parseColor("#1a000000");

    private Paint mPaint;
    private RectF mLoadingBound;
    private RectF mShadowBound;

    private int mTopDegree = 10;
    private int mBottomDegree = 190;
    private int mWidth;
    private int mShadowPosition;
    private int mColor;
    private int mSpeedOfDegree;
    private float mSpeedOfArc;
    private float mArc;
    private boolean mChangeBigger = true;
    private boolean mStart = false;

    public RotateLoadingView(Context context) {
        super(context);
        initView(context, null);
    }

    public RotateLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public RotateLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mColor = Color.WHITE;
        mWidth = dpToPx(context, DEFAULT_WIDTH);
        mShadowPosition = dpToPx(getContext(), DEFAULT_SHADOW_POSITION);
        mSpeedOfDegree = DEFAULT_SPEED_OF_DEGREE;
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, android.support.R.styleable.RotateLoadingView);
            mColor = typedArray.getColor(android.support.R.styleable.RotateLoadingView_loading_color, Color.WHITE);
            mWidth = typedArray.getDimensionPixelSize(android.support.R.styleable.RotateLoadingView_loading_width, dpToPx(context, DEFAULT_WIDTH));
            mShadowPosition = typedArray.getInt(android.support.R.styleable.RotateLoadingView_shadow_position, DEFAULT_SHADOW_POSITION);
            mSpeedOfDegree = typedArray.getInt(android.support.R.styleable.RotateLoadingView_loading_speed, DEFAULT_SPEED_OF_DEGREE);
            typedArray.recycle();
        }
        mSpeedOfArc = mSpeedOfDegree / 4;
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mArc = 10;

        mLoadingBound = new RectF(2 * mWidth, 2 * mWidth, w - 2 * mWidth, h - 2 * mWidth);
        mShadowBound = new RectF(2 * mWidth + mShadowPosition, 2 * mWidth + mShadowPosition, w - 2 * mWidth + mShadowPosition, h - 2 * mWidth + mShadowPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isStart()) return;

        render(canvas);

        update();

        invalidate();
    }

    private void update() {
        mTopDegree += mSpeedOfDegree;
        mBottomDegree += mSpeedOfDegree;
        mTopDegree = mTopDegree > 360 ? mTopDegree - 360 : mTopDegree;
        mBottomDegree = mBottomDegree > 360 ? mBottomDegree - 360 : mBottomDegree;

        if (mChangeBigger) {
            if (mArc < 160) {
                mArc += mSpeedOfArc;
                return;
            }
        } else {
            if (mArc > mSpeedOfDegree) {
                mArc -= 2 * mSpeedOfArc;
                return;
            }
        }
        if (mArc >= 160 || mArc <= 10) {
            mChangeBigger = !mChangeBigger;
        }
    }

    private void render(Canvas canvas) {
        drawShadow(canvas);
        drawLoading(canvas);
    }

    private void drawLoading(Canvas canvas) {
        mPaint.setColor(mColor);
        canvas.drawArc(mLoadingBound, mTopDegree, mArc, false, mPaint);
        canvas.drawArc(mLoadingBound, mBottomDegree, mArc, false, mPaint);
    }

    private void drawShadow(Canvas canvas) {
        mPaint.setColor(DEFAULT_SHADOW_COLOR);
        canvas.drawArc(mShadowBound, mTopDegree, mArc, false, mPaint);
        canvas.drawArc(mShadowBound, mBottomDegree, mArc, false, mPaint);
    }

    public void setLoadingColor(int color) {
        this.mColor = color;
    }

    public int getLoadingColor() {
        return mColor;
    }

    public void start() {
        startAnimator();
        mStart = true;
        invalidate();
    }

    public void stop() {
        stopAnimator();
        invalidate();
    }

    public boolean isStart() {
        return mStart;
    }

    private void startAnimator() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0.0f, 1);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0.0f, 1);
        scaleXAnimator.setDuration(300).setInterpolator(new LinearInterpolator());
        scaleYAnimator.setDuration(300).setInterpolator(new LinearInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }

    private void stopAnimator() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1, 0);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1, 0);
        scaleXAnimator.setDuration(300).setInterpolator(new LinearInterpolator());
        scaleYAnimator.setDuration(300).setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mStart = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    public int dpToPx(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

}
