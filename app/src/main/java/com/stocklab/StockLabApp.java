package com.stocklab;

import android.app.Application;

import com.stocklab.data.local.AppDatabase;
import com.stocklab.data.local.AppPreferences;
import com.stocklab.data.remote.StockApiClient;

/**
 * Application class. Holds app-level dependencies (e.g. database).
 * API key is read from Settings (AppPreferences); if not set, "demo" is used.
 */
public class StockLabApp extends Application {

    private static StockLabApp instance;
    private AppDatabase database;
    private AppPreferences appPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = AppDatabase.getInstance(this);
        appPreferences = new AppPreferences(this);
        String key = appPreferences.getApiKey();
        StockApiClient.setApiKey(key != null ? key : "demo");
    }

    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static StockLabApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
