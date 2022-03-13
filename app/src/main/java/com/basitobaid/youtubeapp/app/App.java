package com.basitobaid.youtubeapp.app;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.basitobaid.youtubeapp.utils.AppPreferences;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
        AppPreferences.getPrefernces(getApplicationContext());
    }

}
