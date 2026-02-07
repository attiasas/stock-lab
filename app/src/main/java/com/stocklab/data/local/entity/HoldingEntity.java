package com.stocklab.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for a stock holding in a profile's portfolio.
 */
@Entity(
    tableName = "holdings",
    foreignKeys = @ForeignKey(
        entity = ProfileEntity.class,
        parentColumns = "id",
        childColumns = "profileId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = { @Index("profileId") }
)
public class HoldingEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long profileId;
    public String symbol;
    public String name;
    /** Quantity held. */
    public double quantity;
    /** Average cost per share (for P&L). */
    public double averageCostPerShare;

    public HoldingEntity() {
    }
}
