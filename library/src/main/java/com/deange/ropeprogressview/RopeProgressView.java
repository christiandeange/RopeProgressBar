package com.deange.ropeprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class RopeProgressView extends ProgressBar {

    private final Paint mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float m1Dip;

    private int mPrimaryColor;
    private int mSecondaryColor;
    private float mSlack;

    public RopeProgressView(final Context context) {
        this(context, null);
    }

    public RopeProgressView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RopeProgressView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        m1Dip = getResources().getDisplayMetrics().density;

        float width = dips(8);
        float slack = dips(32);

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
                attrs, R.styleable.RopeProgressView, defStyleAttr, 0);

        if (a != null) {
            primaryColor = a.getColor(R.styleable.RopeProgressView_primaryColor, primaryColor);
            secondaryColor = a.getColor(R.styleable.RopeProgressView_secondaryColor, secondaryColor);
            slack = a.getDimension(R.styleable.RopeProgressView_slack, slack);
            width = a.getDimension(R.styleable.RopeProgressView_strokeWidth, width);

            a.recycle();
        }

        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
        mSlack = slack;

        mLinesPaint.setStrokeWidth(width);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeCap(Paint.Cap.ROUND);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setIndeterminate(false);
        setBackgroundDrawable(null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(final Drawable background) {
        super.setBackgroundDrawable(null);
    }

    @Override
    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final float strokeWidth = getStrokeWidth();
        final int dw = (int) (getPaddingLeft() + getPaddingRight() + strokeWidth);
        final int dh = (int) (getPaddingTop() + getPaddingBottom() + strokeWidth + mSlack);

        setMeasuredDimension(
                resolveSizeAndState(dw, widthMeasureSpec, 0),
                resolveSizeAndState(dh, heightMeasureSpec, 0));
    }

    @Override
    protected synchronized void onDraw(final Canvas canvas) {

        final float radius = getStrokeWidth() / 2;

        final float top = getPaddingTop() + radius;
        final float left = getPaddingLeft() + radius;
        final float end = getWidth() - getPaddingRight() - radius;

        final float offset = (getProgress() / (float) getMax());
        final float slackHeight = perp(offset) * getSlack();
        final float progressEnd = lerp(left, end, offset);

        mLinesPaint.setColor(mSecondaryColor);
        canvas.drawLine(progressEnd, top + slackHeight, end, top, mLinesPaint);

        mLinesPaint.setColor(mPrimaryColor);
        if (progressEnd == left) {
            // Draw the highlghted part as small as possible
            mLinesPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(radius, radius, radius, mLinesPaint);
            mLinesPaint.setStyle(Paint.Style.STROKE);

        } else {
            canvas.drawLine(left, top, progressEnd, top + slackHeight, mLinesPaint);
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

    private float perp(float t) {
        // eh, could be more mathematically accurate to use a catenary function,
        // but the max difference between the two is only 0.005
        return (float) (- Math.pow(2 * t - 1, 2) + 1);
    }

    private float lerp(float v0, float v1, float t) {
        return (t == 1) ? v1 : (v0 + t * (v1 - v0));
    }

    private float dips(final float dips) {
        return dips * m1Dip;
    }

}
