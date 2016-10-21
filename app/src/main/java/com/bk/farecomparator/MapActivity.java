package com.bk.farecomparator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.AMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {

    @BindView(R.id.main_map)
    com.amap.api.maps.MapView mainMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // Amap init
        mainMap.onCreate(savedInstanceState);
        AMap aMap = mainMap.getMap();
        aMap.setTrafficEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainMap.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainMap.onResume();
    }

    @Override
    protected void onPause() {
        mainMap.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainMap.onSaveInstanceState(outState);
    }
}
