package com.cd.statussaver.activity;


import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * Application class that initializes, loads and show ads when activities change states.
 */
public class MyApplication extends Application{
        private static AppOpenManager appOpenManager;

        @Override
        public void onCreate() {
            super.onCreate();
            MobileAds.initialize(
                    this,
                    new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
            appOpenManager = new AppOpenManager(this);
        }
}