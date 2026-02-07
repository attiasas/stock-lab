package com.stocklab.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/**
 * App-level preferences (e.g. API key). Not tied to a profile.
 */
public final class AppPreferences {

    private static final String PREF_NAME = "stocklab_app";
    private static final String KEY_API_KEY = "alpha_vantage_api_key";

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    public String getApiKey() {
        String v = prefs.getString(KEY_API_KEY, null);
        return (v != null && !v.trim().isEmpty()) ? v.trim() : null;
    }

    public void setApiKey(@Nullable String apiKey) {
        prefs.edit().putString(KEY_API_KEY, apiKey != null ? apiKey.trim() : "").apply();
    }
}
