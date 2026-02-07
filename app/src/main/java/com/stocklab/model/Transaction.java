package com.stocklab.model;

/**
 * Domain model for a buy/sell transaction.
 */
public class Transaction {

    public final long id;
    public final String type; // "buy" or "sell"
    public final String symbol;
    public final String name;
    public final double quantity;
    public final double pricePerShare;
    public final long timestamp;

    public Transaction(long id, String type, String symbol, String name,
                       double quantity, double pricePerShare, long timestamp) {
        this.id = id;
        this.type = type;
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = timestamp;
    }
}
