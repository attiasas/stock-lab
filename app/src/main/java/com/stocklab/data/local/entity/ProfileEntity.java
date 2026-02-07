package com.stocklab.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity for a simulation profile (like a game save).
 */
@Entity(tableName = "profiles")
public class ProfileEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public long createdAt;

    /** Starting cash in smallest currency unit (e.g. cents). */
    public long startingCash;
    /** Currency code (e.g. USD). */
    public String currency;
    /** Difficulty: 0 easy, 1 normal, 2 hard. Affects simulation params. */
    public int difficulty;

    /** Current cash balance in smallest unit. */
    public long currentCash;

    public ProfileEntity() {
    }
}
