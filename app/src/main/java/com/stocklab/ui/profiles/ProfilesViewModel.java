package com.stocklab.ui.profiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.ProfileRepository;
import com.stocklab.model.Profile;

import java.util.List;

public class ProfilesViewModel extends ViewModel {

    private ProfileRepository repository;
    private LiveData<List<Profile>> profiles;

    public void init(android.content.Context context) {
        if (repository == null) {
            repository = new ProfileRepository(context);
            profiles = repository.getAllProfiles();
        }
    }

    public LiveData<List<Profile>> getProfiles() {
        return profiles;
    }

    public void deleteProfile(long id) {
        if (repository != null) repository.deleteProfile(id);
    }
}
