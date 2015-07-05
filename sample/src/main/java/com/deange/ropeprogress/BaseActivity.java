package com.deange.ropeprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import com.deange.ropeprogressview.RopeProgressBar;

public abstract class BaseActivity
        extends AppCompatActivity
        implements
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    private EditText mEditText;
    private RopeProgressBar mRopeProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mRopeProgressBar = findRopeProgressView(getWindow().getDecorView());

        ((SeekBar) findViewById(R.id.progress_slider)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_animate).setOnClickListener(this);

        mEditText = (EditText) findViewById(R.id.progress_jump);
    }

    protected RopeProgressBar getRopeProgressBar() {
        return mRopeProgressBar;
    }

    private RopeProgressBar findRopeProgressView(final View view) {
        if (view instanceof RopeProgressBar) {
            return (RopeProgressBar) view;

        } else if (view instanceof ViewGroup) {
            ViewGroup p = ((ViewGroup) view);
            View child;
            for (int i = 0; i < p.getChildCount(); i++) {
                if ((child = findRopeProgressView(p.getChildAt(i))) != null) {
                    return (RopeProgressBar) child;
                }
            }
        }

        return null;
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean user) {
        mRopeProgressBar.setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        mRopeProgressBar.defer();
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        mRopeProgressBar.endDefer();
    }

    @Override
    public void onClick(final View v) {
        final Integer progress = Integer.parseInt(mEditText.getText().toString());
        mRopeProgressBar.animateProgress(progress);
    }

    public abstract int getLayoutId();
}
