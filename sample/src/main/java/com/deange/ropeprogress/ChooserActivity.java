package com.deange.ropeprogress;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChooserActivity
        extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final List<String> mLabels = new ArrayList<>();
    private final List<Class> mActivities = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        try {
            final ActivityInfo[] activityInfos = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES).activities;

            for (final ActivityInfo activity : activityInfos) {
                final Class<?> clazz = Class.forName(activity.name);
                if (BaseActivity.class.isAssignableFrom(clazz)) {
                    mLabels.add(getString(activity.labelRes));
                    mActivities.add(clazz);
                }
            }

        } catch (final Exception e) {
            Log.w("ChooserActivity", "Supressed exception while getting activity list", e);
        }

        final ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new TypesAdapter());
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(
            final AdapterView<?> parent,
            final View view,
            final int position,
            final long id) {
        startActivity(new Intent(this, mActivities.get(position)));
    }

    private class TypesAdapter extends ArrayAdapter<String> {
        public TypesAdapter() {
            super(ChooserActivity.this, android.R.layout.simple_list_item_1, mLabels);
        }
    }
}
