package com.bk.farecomparator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {

    @BindView(R.id.main_map)
    MapView mainMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // Amap init
        mainMap.onCreate(savedInstanceState);
        mainMap.getMap().setTrafficEnabled(true);
        mainMap.getMap().getUiSettings().setZoomControlsEnabled(false);
        mainMap.getMap().getUiSettings().setCompassEnabled(true);
        mainMap.getMap().getUiSettings().setScaleControlsEnabled(true);
        mainMap.getMap().setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                // TODO: integrate location sdk
            }

            @Override
            public void deactivate() {

            }
        });
        mainMap.getMap().getUiSettings().setMyLocationButtonEnabled(true);
        mainMap.getMap().setMyLocationEnabled(true);
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
        super.onPause();
        mainMap.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainMap.onSaveInstanceState(outState);
    }

}
