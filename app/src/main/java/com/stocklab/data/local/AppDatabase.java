package com.stocklab.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.stocklab.data.local.dao.HoldingDao;
import com.stocklab.data.local.dao.ProfileDao;
import com.stocklab.data.local.dao.TransactionDao;
import com.stocklab.data.local.entity.HoldingEntity;
import com.stocklab.data.local.entity.ProfileEntity;
import com.stocklab.data.local.entity.TransactionEntity;

@Database(
    entities = { ProfileEntity.class, HoldingEntity.class, TransactionEntity.class },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract ProfileDao profileDao();
    public abstract HoldingDao holdingDao();
    public abstract TransactionDao transactionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "stocklab.db"
                    ).allowMainThreadQueries().build();
                }
            }
        }
        return instance;
    }
}
