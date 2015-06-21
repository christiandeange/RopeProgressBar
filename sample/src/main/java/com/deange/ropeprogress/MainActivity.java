package com.deange.ropeprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.deange.ropeprogressview.RopeProgressView;

public class MainActivity
        extends AppCompatActivity
        implements
        SeekBar.OnSeekBarChangeListener {

    private RopeProgressView mRopeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRopeView = (RopeProgressView) findViewById(R.id.rope_progress_view);

        ((SeekBar) findViewById(R.id.progress_slider)).setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        mRopeView.setProgress(progress);
    }

    @Override public void onStartTrackingTouch(final SeekBar seekBar) { }

    @Override public void onStopTrackingTouch(final SeekBar seekBar) { }
}
