package com.udacity.android.popularmovies.app;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.Stetho;

public class PopularApplication extends Application {
    private static PopularApplication popularApplication;
    private static Context context;

    public static PopularApplication getMyApplication() {
        return popularApplication;
    }

    public static Context getAppContext() {
        return PopularApplication.context;
    }

    public void onCreate() {
        super.onCreate();

        popularApplication = this;
        PopularApplication.context = getApplicationContext();

        initializeStetho();

    }

    public void initializeStetho() {
        Stetho.initializeWithDefaults(context);
    }
}
