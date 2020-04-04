package com.check.apps.checkapp.language;

import android.content.Context;

import androidx.multidex.MultiDexApplication;


public class App extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.updateResources(base,"en"));
    }

  public void onCreate() {
        super.onCreate();

    }
}
