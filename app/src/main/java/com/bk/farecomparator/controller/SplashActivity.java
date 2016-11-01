package com.bk.farecomparator.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start MapActivity
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }
}
