package com.deange.ropeprogress;

import android.os.Bundle;

import com.deange.ropeprogressview.ProgressFormatter;

public class CustomTextRopeActivity
        extends BaseActivity
        implements
        ProgressFormatter {

    @Override
    public int getLayoutId() {
        return R.layout.activity_static;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRopeProgressBar().setProgressFormatter(this);
    }

    @Override
    public String getFormattedText(final int progress, final int max) {
        return progress + "/" + max;
    }

}
