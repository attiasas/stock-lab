package com.stocklab;

import android.app.Application;

import com.stocklab.data.local.AppDatabase;
import com.stocklab.data.remote.StockApiClient;

/**
 * Application class. Holds app-level dependencies (e.g. database).
 */
public class StockLabApp extends Application {

    private static StockLabApp instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = AppDatabase.getInstance(this);
        StockApiClient.setApiKey(BuildConfig.STOCK_API_KEY);
    }

    public static StockLabApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
