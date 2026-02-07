package com.stocklab.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.stocklab.data.local.entity.TransactionEntity;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE profileId = :profileId ORDER BY timestamp DESC")
    LiveData<List<TransactionEntity>> getTransactionsByProfile(long profileId);

    @Query("SELECT * FROM transactions WHERE profileId = :profileId ORDER BY timestamp DESC")
    List<TransactionEntity> getTransactionsByProfileSync(long profileId);

    @Insert
    long insert(TransactionEntity transaction);
}
