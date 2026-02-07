package com.stocklab.model;

/**
 * Real-time stock quote (from API).
 */
public class StockQuote {

    public final String symbol;
    public final double price;
    public final double open;
    public final double high;
    public final double low;
    public final double previousClose;
    public final double change;
    public final double changePercent;
    public final String latestTradingDay;

    public StockQuote(String symbol, double price, double open, double high, double low,
                      double previousClose, double change, double changePercent,
                      String latestTradingDay) {
        this.symbol = symbol;
        this.price = price;
        this.open = open;
        this.high = high;
        this.low = low;
        this.previousClose = previousClose;
        this.change = change;
        this.changePercent = changePercent;
        this.latestTradingDay = latestTradingDay;
    }
}
