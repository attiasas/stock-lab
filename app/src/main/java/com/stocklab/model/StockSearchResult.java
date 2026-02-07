package com.stocklab.model;

/**
 * A stock symbol search result (from API).
 */
public class StockSearchResult {

    public final String symbol;
    public final String name;
    public final String type;
    public final String region;
    public final String currency;

    public StockSearchResult(String symbol, String name, String type, String region, String currency) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.region = region;
        this.currency = currency;
    }
}
