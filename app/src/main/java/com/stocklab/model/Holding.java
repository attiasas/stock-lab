package com.stocklab.model;

/**
 * Domain model for a portfolio holding (stock position).
 */
public class Holding {

    public final long id;
    public final String symbol;
    public final String name;
    public final double quantity;
    public final double averageCostPerShare;
    /** Current price from API (set when displaying). */
    public double currentPrice;

    public Holding(long id, String symbol, String name, double quantity,
                   double averageCostPerShare, double currentPrice) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.averageCostPerShare = averageCostPerShare;
        this.currentPrice = currentPrice;
    }
}
