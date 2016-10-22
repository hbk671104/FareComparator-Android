package com.bk.farecomparator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {

    @BindView(R.id.main_map)
    MapView mainMap;
    private AMapLocationClient aMapLocationClient;
    private LocationSource.OnLocationChangedListener locationChangedListener;

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
                locationChangedListener = onLocationChangedListener;
                aMapLocationClient.startLocation();
            }

            @Override
            public void deactivate() {
                locationChangedListener = null;
                if (aMapLocationClient != null) {
                    aMapLocationClient.stopLocation();
                    aMapLocationClient.onDestroy();
                }
                aMapLocationClient = null;
            }
        });
        mainMap.getMap().getUiSettings().setMyLocationButtonEnabled(true);
        mainMap.getMap().setMyLocationEnabled(true);
        mainMap.getMap().setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        // Amap location client init
        aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (locationChangedListener != null && aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        locationChangedListener.onLocationChanged(aMapLocation);
                        mainMap.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                        aMapLocation.getLatitude(),
                                        aMapLocation.getLongitude()),
                                15));
                    } else {
                        String errText = aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                        Toast.makeText(MapActivity.this, errText, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClient.setLocationOption(option);
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
