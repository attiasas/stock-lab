package com.stocklab.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.stocklab.R;
import com.stocklab.StockLabApp;
import com.stocklab.data.local.AppPreferences;
import com.stocklab.data.remote.StockApiClient;

public class SettingsFragment extends Fragment {

    private TextInputEditText inputApiKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputApiKey = view.findViewById(R.id.input_api_key);
        AppPreferences prefs = StockLabApp.getInstance().getAppPreferences();
        if (prefs != null) {
            String current = prefs.getApiKey();
            if (current != null) inputApiKey.setText(current);
        }

        view.findViewById(R.id.btn_save_api_key).setOnClickListener(v -> saveApiKey());
        view.findViewById(R.id.link_get_api_key).setOnClickListener(v -> openApiKeyUrl());
    }

    private void openApiKeyUrl() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.alphavantage.co/support/#api-key")));
        } catch (Exception ignored) {
        }
    }

    private void saveApiKey() {
        String key = inputApiKey.getText() != null ? inputApiKey.getText().toString().trim() : "";
        AppPreferences prefs = StockLabApp.getInstance().getAppPreferences();
        if (prefs != null) prefs.setApiKey(key.isEmpty() ? null : key);
        StockApiClient.setApiKey(key.isEmpty() ? "demo" : key);
        Toast.makeText(requireContext(), R.string.api_key_saved, Toast.LENGTH_SHORT).show();
    }
}
