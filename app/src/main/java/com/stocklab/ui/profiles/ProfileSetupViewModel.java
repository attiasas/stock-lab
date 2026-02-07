package com.stocklab.ui.profiles;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.ProfileRepository;

public class ProfileSetupViewModel extends ViewModel {

    private ProfileRepository repository;

    public void init(Context context) {
        if (repository == null) {
            repository = new ProfileRepository(context);
        }
    }

    public long createProfile(String name, long startingCashCents, String currency, int difficulty) {
        if (repository == null) return -1;
        return repository.createProfile(name, startingCashCents, currency, difficulty);
    }
}
