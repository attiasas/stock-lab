package com.stocklab.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for a buy/sell transaction (simulated).
 */
@Entity(
    tableName = "transactions",
    foreignKeys = @ForeignKey(
        entity = ProfileEntity.class,
        parentColumns = "id",
        childColumns = "profileId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = { @Index("profileId"), @Index("timestamp") }
)
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long profileId;
    /** "buy" or "sell". */
    public String type;
    public String symbol;
    public String name;
    public double quantity;
    public double pricePerShare;
    public long timestamp;

    public TransactionEntity() {
    }
}
