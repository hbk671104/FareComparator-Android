package com.bk.farecomparator;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * Created by BK on 22/10/2016.
 */

public class FCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }
}
