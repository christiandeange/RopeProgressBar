package com.deange.ropeprogress;

import android.graphics.Paint;
import android.os.Bundle;

public class ThemedRopeActivity
        extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_themed;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Paint paint = getRopeProgressBar().getTextPaint();
        paint.setColor(getResources().getColor(R.color.material_deep_teal_500));
        paint.setFakeBoldText(true);
        getRopeProgressBar().setTextPaint(paint);
    }
}
