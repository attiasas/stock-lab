package com.stocklab.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.stocklab.data.local.entity.ProfileEntity;

import java.util.List;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY createdAt DESC")
    LiveData<List<ProfileEntity>> getAllProfiles();

    @Query("SELECT * FROM profiles WHERE id = :id")
    LiveData<ProfileEntity> getProfileById(long id);

    @Query("SELECT * FROM profiles WHERE id = :id")
    ProfileEntity getProfileByIdSync(long id);

    @Insert
    long insert(ProfileEntity profile);

    @Update
    void update(ProfileEntity profile);

    @Delete
    void delete(ProfileEntity profile);

    @Query("DELETE FROM profiles WHERE id = :id")
    void deleteById(long id);
}
