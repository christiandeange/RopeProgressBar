package com.deange.ropeprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class RopeProgressBar extends View {

    private final Paint mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float m1Dip;
    private final float m1Sp;
    private int mProgress;
    private int mMax;

    private int mPrimaryColor;
    private int mSecondaryColor;
    private float mSlack;
    private boolean mDynamicLayout;

    private final Rect mBounds = new Rect();
    private final Path mBubble = new Path();
    private final Path mTriangle = new Path();

    private final Runnable mRequestLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    public RopeProgressBar(final Context context) {
        this(context, null);
    }

    public RopeProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RopeProgressBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        m1Dip = getResources().getDisplayMetrics().density;
        m1Sp = getResources().getDisplayMetrics().scaledDensity;

        int max = 0;
        int progress = 0;

        float width = dips(8);
        float slack = dips(32);
        boolean dynamicLayout = false;

        int primaryColor = 0xFF009688;
        int secondaryColor = 0xFFDADADA;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final TypedValue out = new TypedValue();

            context.getTheme().resolveAttribute(R.attr.colorControlActivated, out, true);
            primaryColor = out.data;
            context.getTheme().resolveAttribute(R.attr.colorControlHighlight, out, true);
            secondaryColor = out.data;
        }

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RopeProgressBar, defStyleAttr, 0);

        if (a != null) {
            max = a.getInt(R.styleable.RopeProgressBar_max, max);
            progress = a.getInt(R.styleable.RopeProgressBar_progress, progress);

            primaryColor = a.getColor(R.styleable.RopeProgressBar_primaryColor, primaryColor);
            secondaryColor = a.getColor(R.styleable.RopeProgressBar_secondaryColor, secondaryColor);
            slack = a.getDimension(R.styleable.RopeProgressBar_slack, slack);
            width = a.getDimension(R.styleable.RopeProgressBar_strokeWidth, width);
            dynamicLayout = a.getBoolean(R.styleable.RopeProgressBar_dynamicLayout, false);

            a.recycle();
        }

        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
        mSlack = slack;
        mDynamicLayout = dynamicLayout;

        mLinesPaint.setStrokeWidth(width);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeCap(Paint.Cap.ROUND);

        mBubblePaint.setColor(Color.WHITE);
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setPathEffect(new CornerPathEffect(dips(2)));

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(sp(18));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create("sans-serif-condensed-light", 0));

        setMax(max);
        setProgress(progress);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(final Drawable background) {
        super.setBackgroundDrawable(null);
    }

    private void dynamicRequestLayout() {
        if (mDynamicLayout) {
            // We need to calculate our new height, since the progress affect the slack
            if (Looper.getMainLooper() == Looper.myLooper()) {
                requestLayout();
            } else {
                post(mRequestLayoutRunnable);
            }
        }
    }

    @Override
    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        // Recalculate how tall the text needs to be
        //noinspection ReplaceAllDot
        final String maxString = String.valueOf(getMax()).replaceAll(".", "8");
        mTextPaint.getTextBounds(maxString, 0, maxString.length(), mBounds);

        final int bubbleHeight = (int) Math.ceil(getBubbleVerticalDisplacement());
        final float slack = mDynamicLayout ? getCurrentSlackHeight() : getSlack();

        final float strokeWidth = getStrokeWidth();
        final int dw = (int) Math.ceil(getPaddingLeft() + getPaddingRight() + strokeWidth);
        final int dh = (int) Math.ceil(getPaddingTop() + getPaddingBottom() + strokeWidth + slack);

        setMeasuredDimension(
                resolveSizeAndState(dw, widthMeasureSpec, 0),
                resolveSizeAndState(dh + bubbleHeight, heightMeasureSpec, 0));

        makeBubble();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    protected synchronized void onDraw(final Canvas canvas) {

        final float radius = getStrokeWidth() / 2;

        final float bubbleDisplacement = getBubbleVerticalDisplacement();
        final float top = getPaddingTop() + radius + bubbleDisplacement;
        final float left = getPaddingLeft() + radius;
        final float end = getWidth() - getPaddingRight() - radius;

        final float max = getMax();
        final float offset = (max == 0) ? 0 : (getProgress() / max);
        final float slackHeight = getCurrentSlackHeight();
        final float progressEnd = lerp(left, end, offset);

        // Draw the secondary background line
        mLinesPaint.setColor(mSecondaryColor);
        canvas.drawLine(progressEnd, top + slackHeight, end, top, mLinesPaint);

        // Draw the primary progress line
        mLinesPaint.setColor(mPrimaryColor);
        if (progressEnd == left) {
            // Draw the highlghted part as small as possible
            mLinesPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, radius, mLinesPaint);
            mLinesPaint.setStyle(Paint.Style.STROKE);

        } else {
            canvas.drawLine(left, top, progressEnd, top + slackHeight, mLinesPaint);
        }

        // Draw the bubble text background
        final float bubbleWidth = getBubbleWidth();
        final float bubbleHeight = getBubbleHeight();

        final float bubbleLeft = Math.min(
                getWidth() - bubbleWidth, Math.max(
                        0, progressEnd - (bubbleWidth / 2)));
        final float bubbleTop = slackHeight;

        mBubble.offset(bubbleLeft, bubbleTop);
        canvas.drawPath(mBubble, mBubblePaint);
        mBubble.offset(-bubbleLeft, -bubbleTop);

        // Draw the triangle part of the bubble
        final float triangleLeft = Math.min(
                getWidth() - getTriangleWidth(), Math.max(
                        0, progressEnd - (getTriangleWidth() / 2)));
        final float triangleTop = bubbleTop + bubbleHeight;

        mTriangle.offset(triangleLeft, triangleTop);
        canvas.drawPath(mTriangle, mBubblePaint);
        mTriangle.offset(-triangleLeft, -triangleTop);

        // Draw the progress text part of the bubble
        final float textX = bubbleLeft + bubbleWidth / 2;
        final float textY = bubbleTop + bubbleHeight - dips(8);
        final String progress = String.valueOf(getProgress());

        canvas.drawText(progress, textX, textY, mTextPaint);
    }

    private float getCurrentSlackHeight() {
        final float max = getMax();
        final float offset = (max == 0) ? 0 : (getProgress() / max);
        return perp(offset) * getSlack();
    }

    private float getBubbleVerticalDisplacement() {
        return getBubbleMargin() + getBubbleHeight() + getTriangleHeight();
    }

    public float getBubbleMargin() {
        return dips(4);
    }

    public float getBubbleWidth() {
        return mBounds.width() + /* padding */ dips(16);
    }

    public float getBubbleHeight() {
        return mBounds.height() + /* padding */ dips(16);
    }

    public float getTriangleWidth() {
        return dips(12);
    }

    public float getTriangleHeight() {
        return dips(6);
    }

    private void makeBubble() {

        final float bubbleWidth = getBubbleWidth();
        final float bubbleHeight = getBubbleHeight();

        final float triangleWidth = getTriangleWidth();
        final float triangleHeight = getTriangleHeight();
        final float triangleTop = 0;
        final float triangleLeft = 0;

        mTriangle.reset();
        mTriangle.moveTo(triangleLeft, triangleTop);
        mTriangle.lineTo(triangleLeft + triangleWidth, triangleTop);
        mTriangle.lineTo(triangleLeft + triangleWidth / 2f, triangleHeight);
        mTriangle.lineTo(triangleLeft, triangleTop);

        mBubble.reset();
        mBubble.moveTo(0, 0);
        mBubble.addRect(0, 0, bubbleWidth, bubbleHeight, Path.Direction.CW);
    }

    public synchronized void setProgress(int progress) {
        progress = Math.max(0, Math.min(getMax(), progress));
        if (progress == mProgress) {
            return;
        }

        dynamicRequestLayout();
        mProgress = progress;
        postInvalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setMax(int max) {
        max = Math.max(0, max);

        if (max != mMax) {

            dynamicRequestLayout();
            mMax = max;
            postInvalidate();

            if (mProgress > max) {
                mProgress = max;
            }
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setDynamicLayout(final boolean isDynamic) {
        if (mDynamicLayout != isDynamic) {
            mDynamicLayout = isDynamic;

            requestLayout();
            invalidate();
        }
    }

    public void setPrimaryColor(final int color) {
        mPrimaryColor = color;

        invalidate();
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setSecondaryColor(final int color) {
        mSecondaryColor = color;

        invalidate();
    }

    public int getSecondaryColor() {
        return mSecondaryColor;
    }

    public void setSlack(final float slack) {
        mSlack = slack;

        requestLayout();
        invalidate();
    }

    public float getSlack() {
        return mSlack;
    }

    public void setStrokeWidth(final float width) {
        mLinesPaint.setStrokeWidth(width);

        requestLayout();
        invalidate();
    }

    public float getStrokeWidth() {
        return mLinesPaint.getStrokeWidth();
    }

    public void setTextPaint(final Paint paint) {
        mTextPaint.set(paint);

        requestLayout();
        invalidate();
    }

    /**
     * Return a copy so that fields can only be modified through {@link #setTextPaint}
     */
    public Paint getTextPaint() {
        return new Paint(mTextPaint);
    }

    private float perp(float t) {
        // eh, could be more mathematically accurate to use a catenary function,
        // but the max difference between the two is only 0.005
        return (float) (-Math.pow(2 * t - 1, 2) + 1);
    }

    private float lerp(float v0, float v1, float t) {
        return (t == 1) ? v1 : (v0 + t * (v1 - v0));
    }

    private float dips(final float dips) {
        return dips * m1Dip;
    }

    private float sp(final int sp) {
        return sp * m1Sp;
    }

}
