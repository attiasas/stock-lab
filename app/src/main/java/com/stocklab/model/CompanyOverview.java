package com.stocklab.model;

/**
 * Company fundamental/overview data for display.
 */
public class CompanyOverview {
    public final String symbol;
    public final String name;
    public final String description;
    public final String sector;
    public final String industry;
    public final String exchange;
    public final String currency;
    public final String country;
    public final String marketCap;
    public final String peRatio;
    public final String week52High;
    public final String week52Low;
    public final String dividendYield;
    public final String eps;
    public final String analystTargetPrice;

    public CompanyOverview(String symbol, String name, String description, String sector, String industry,
                           String exchange, String currency, String country, String marketCap, String peRatio,
                           String week52High, String week52Low, String dividendYield, String eps, String analystTargetPrice) {
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.industry = industry;
        this.exchange = exchange;
        this.currency = currency;
        this.country = country;
        this.marketCap = marketCap;
        this.peRatio = peRatio;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.dividendYield = dividendYield;
        this.eps = eps;
        this.analystTargetPrice = analystTargetPrice;
    }
}
