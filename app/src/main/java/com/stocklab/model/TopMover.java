package com.stocklab.model;

/**
 * Top gainer, loser, or most actively traded ticker.
 */
public class TopMover {
    public final String ticker;
    public final double price;
    public final double changeAmount;
    public final double changePercentage;
    public final long volume;

    public TopMover(String ticker, double price, double changeAmount, double changePercentage, long volume) {
        this.ticker = ticker;
        this.price = price;
        this.changeAmount = changeAmount;
        this.changePercentage = changePercentage;
        this.volume = volume;
    }
}
