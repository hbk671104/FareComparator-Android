package com.bk.farecomparator.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bk.farecomparator.R;
import com.bk.farecomparator.adapter.PoiResultAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity implements AMapLocationListener,
        View.OnClickListener, AMap.OnCameraChangeListener,
        SearchView.OnQueryTextListener, PoiSearch.OnPoiSearchListener {

    @BindView(R.id.main_map)
    MapView mainMap;
    @BindView(R.id.user_location_button)
    ImageButton locateUserButton;
    @BindView(R.id.compare_price_button)
    Button comparePriceButton;
    @BindView(R.id.poi_search_list_view)
    ListView poiSearchListView;

    // Location stuff
    private AMapLocationClient aMapLocationClient;
    private LatLng userLatLng;
    private boolean isFirstLoc = true;
    private boolean isFromResetUserLocation = false;
    private String cityName;
    private String provinceName;

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

        // Action bar init
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);

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

                // Camera change listener
                mainMap.getMap().setOnCameraChangeListener(MapActivity.this);
                // Locate user button
                locateUserButton.setVisibility(View.VISIBLE);
                comparePriceButton.setVisibility(View.VISIBLE);
                togglePriceCompareButton();
                locateUserButton.setOnClickListener(MapActivity.this);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                locateUserButton.setVisibility(View.GONE);
                comparePriceButton.setVisibility(View.GONE);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        // Set action bar width
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            searchView.setMaxWidth(mainMap.getWidth() - actionBarHeight);
        }
        // Query text listener
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
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

    // MARK: - Instance method

    private void togglePriceCompareButton() {
        if (comparePriceButton.isEnabled()) {
            comparePriceButton.setClickable(false);
            comparePriceButton.setTextColor(Color.GRAY);
        } else {
            comparePriceButton.setClickable(true);
            comparePriceButton.setTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(
                            MapActivity.this, R.color.locationPrimaryColor
                    )
            ));
        }
    }

    private void resetUserLocation(LatLng latLng) {
        isFromResetUserLocation = true;
        mainMap.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void startPOIQuery(PoiSearch.Query query) {
        PoiSearch search = new PoiSearch(this, query);
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
    }

    // MARK: - AMapLocationListener

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                if (isFirstLoc) {
                    isFirstLoc = !isFirstLoc;
                    userLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    cityName = aMapLocation.getCity();
                    provinceName = aMapLocation.getProvince();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(userLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
                    mainMap.getMap().addMarker(markerOptions);
                    resetUserLocation(userLatLng);
                }
            } else {
                Log.e("Error", aMapLocation.getErrorInfo());
            }
        }
    }

    // MARK: - AMap.OnCameraChangeListener

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        ColorStateList stateList = ColorStateList.valueOf(Color.GRAY);
        if (locateUserButton.getImageTintList() != stateList) {
            locateUserButton.setImageTintList(stateList);
            isFromResetUserLocation = false;
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isFromResetUserLocation) {
            locateUserButton.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(
                                    MapActivity.this, R.color.locationPrimaryColor
                            )
                    )
            );
        }
    }

    // MARK: - View.OnClickListener

    @Override
    public void onClick(View view) {
        if (view instanceof ImageButton) {
            resetUserLocation(userLatLng);
        }
    }

    // MARK: - SearchView.OnQueryTextListener

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            poiSearchListView.setVisibility(View.VISIBLE);
            String region = (cityName.isEmpty() || cityName == null) ? provinceName : cityName;
            PoiSearch.Query query = new PoiSearch.Query(newText, "", region);
            query.setCityLimit(true);
            startPOIQuery(query);
        } else {
            poiSearchListView.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // MARK: - PoiSearch.OnPoiSearchListener

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {
                poiSearchListView.setAdapter(new PoiResultAdapter(this, R.layout.poi_item, poiResult.getPois()));
            }
        } else {
            Log.e("Poi检索错误", "Code: " + i);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

}