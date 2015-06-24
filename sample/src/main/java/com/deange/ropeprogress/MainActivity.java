package com.deange.ropeprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.deange.ropeprogressview.RopeProgressBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements
        SeekBar.OnSeekBarChangeListener {

    private final List<RopeProgressBar> mRopeProgressBars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findRopeProgressViews(getWindow().getDecorView());

        ((SeekBar) findViewById(R.id.progress_slider)).setOnSeekBarChangeListener(this);
    }

    private void findRopeProgressViews(final View view) {
        if (view instanceof RopeProgressBar) {
            mRopeProgressBars.add((RopeProgressBar) view);

        } else if (view instanceof ViewGroup) {
            ViewGroup p = ((ViewGroup) view);
            for (int i = 0; i < p.getChildCount(); i++) {
                findRopeProgressViews(p.getChildAt(i));
            }
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean user) {
        for (final RopeProgressBar view : mRopeProgressBars) {
            view.setProgress(progress);
        }
    }

    @Override public void onStartTrackingTouch(final SeekBar seekBar) { }

    @Override public void onStopTrackingTouch(final SeekBar seekBar) { }
}
