package com.stocklab.model;

/**
 * Single day OHLCV for time series display.
 */
public class DailyOhlc {
    public final String date;
    public final double open;
    public final double high;
    public final double low;
    public final double close;
    public final long volume;

    public DailyOhlc(String date, double open, double high, double low, double close, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}
