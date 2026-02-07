package com.stocklab.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.stocklab.data.local.entity.HoldingEntity;

import java.util.List;

@Dao
public interface HoldingDao {

    @Query("SELECT * FROM holdings WHERE profileId = :profileId ORDER BY symbol")
    LiveData<List<HoldingEntity>> getHoldingsByProfile(long profileId);

    @Query("SELECT * FROM holdings WHERE profileId = :profileId ORDER BY symbol")
    List<HoldingEntity> getHoldingsByProfileSync(long profileId);

    @Query("SELECT * FROM holdings WHERE profileId = :profileId AND symbol = :symbol LIMIT 1")
    HoldingEntity getHoldingSync(long profileId, String symbol);

    @Insert
    long insert(HoldingEntity holding);

    @Update
    void update(HoldingEntity holding);

    @Delete
    void delete(HoldingEntity holding);

    @Query("DELETE FROM holdings WHERE profileId = :profileId")
    void deleteByProfile(long profileId);
}
