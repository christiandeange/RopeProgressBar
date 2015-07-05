package com.deange.ropeprogressview;

import android.view.animation.Interpolator;

/* package */ class DampingInterpolator implements Interpolator {

    private final float mCycles;

    public DampingInterpolator() {
        this(1);
    }

    public DampingInterpolator(float cycles) {
        mCycles = cycles;
    }

    @Override
    public float getInterpolation(final float input) {
        return (float) (Math.sin(mCycles * 2 * Math.PI * input) * ((input - 1) * (input - 1)));
    }
}
