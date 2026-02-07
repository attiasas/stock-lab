package com.stocklab.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.stocklab.StockLabApp;
import com.stocklab.data.local.dao.ProfileDao;
import com.stocklab.data.local.entity.ProfileEntity;
import com.stocklab.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for profile CRUD and current profile preference.
 */
public class ProfileRepository {

    private static final String PREF_CURRENT_PROFILE = "current_profile_id";
    private static final String PREF_NAME = "stocklab_prefs";

    private final ProfileDao profileDao;
    private final Context context;

    public ProfileRepository(Context context) {
        this.context = context;
        this.profileDao = StockLabApp.getInstance().getDatabase().profileDao();
    }

    public LiveData<List<Profile>> getAllProfiles() {
        return Transformations.map(profileDao.getAllProfiles(), this::toProfiles);
    }

    public LiveData<Profile> getProfileById(long id) {
        return Transformations.map(profileDao.getProfileById(id), this::toProfile);
    }

    public Profile getProfileByIdSync(long id) {
        ProfileEntity e = profileDao.getProfileByIdSync(id);
        return e == null ? null : toProfile(e);
    }

    public long createProfile(String name, long startingCash, String currency, int difficulty) {
        ProfileEntity e = new ProfileEntity();
        e.name = name;
        e.createdAt = System.currentTimeMillis();
        e.startingCash = startingCash;
        e.currentCash = startingCash;
        e.currency = currency;
        e.difficulty = difficulty;
        return profileDao.insert(e);
    }

    public void deleteProfile(long id) {
        profileDao.deleteById(id);
        if (getCurrentProfileId() == id) {
            setCurrentProfileId(-1);
        }
    }

    public void updateProfileCash(long profileId, long newCash) {
        ProfileEntity e = profileDao.getProfileByIdSync(profileId);
        if (e != null) {
            e.currentCash = newCash;
            profileDao.update(e);
        }
    }

    public long getCurrentProfileId() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(PREF_CURRENT_PROFILE, -1);
    }

    public void setCurrentProfileId(long id) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(PREF_CURRENT_PROFILE, id)
            .apply();
    }

    private List<Profile> toProfiles(List<ProfileEntity> entities) {
        List<Profile> list = new ArrayList<>();
        if (entities != null) {
            for (ProfileEntity e : entities) {
                list.add(toProfile(e));
            }
        }
        return list;
    }

    private Profile toProfile(ProfileEntity e) {
        if (e == null) return null;
        return new Profile(e.id, e.name, e.createdAt, e.startingCash, e.currency,
            e.difficulty, e.currentCash);
    }
}
