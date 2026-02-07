package com.stocklab.data.remote;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides Retrofit StockApi instance. Uses Alpha Vantage base URL.
 * API key should be set via BuildConfig or passed when creating service.
 */
public final class StockApiClient {

    private static final String BASE_URL = "https://www.alphavantage.co/";

    private static StockApi api;
    private static String apiKey = "demo"; // Replace with your key; use BuildConfig in production

    private StockApiClient() {
    }

    public static void setApiKey(String key) {
        apiKey = key != null && !key.isEmpty() ? key : "demo";
        api = null; // force rebuild with new key
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static StockApi getApi() {
        if (api == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                message -> Log.d("StockApi", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            api = retrofit.create(StockApi.class);
        }
        return api;
    }
}
