package com.bk.farecomparator;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity implements AMapLocationListener {

    @BindView(R.id.main_map)
    MapView mainMap;

    // Location stuff
    private AMapLocationClient aMapLocationClient;
    private LatLng userLatLng;
    private boolean isFirstLoc = true;

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

        // Check permissions
        Dexter.checkPermissions(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                aMapLocationClient = new AMapLocationClient(MapActivity.this);
                aMapLocationClient.setLocationListener(MapActivity.this);
                AMapLocationClientOption option = new AMapLocationClientOption();
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                option.setNeedAddress(true);
                aMapLocationClient.setLocationOption(option);
                aMapLocationClient.startLocation();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainMap.onDestroy();
        if (aMapLocationClient != null) {
            aMapLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainMap.onResume();
        if (aMapLocationClient != null) {
            aMapLocationClient.startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainMap.onPause();
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainMap.onSaveInstanceState(outState);
    }

    // MARK: - AMapLocationListener

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                if (isFirstLoc) {
                    isFirstLoc = !isFirstLoc;
                    userLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(userLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
                    mainMap.getMap().addMarker(markerOptions);
                    mainMap.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }
            } else {
                Log.e("Error", aMapLocation.getErrorCode() + "");
            }
        }
    }
}