package com.stocklab.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Alpha Vantage OVERVIEW (company fundamental data).
 * https://www.alphavantage.co/documentation/#company-overview
 */
public class CompanyOverviewDto {

    @SerializedName("Symbol")
    public String symbol;
    @SerializedName("Name")
    public String name;
    @SerializedName("Description")
    public String description;
    @SerializedName("Sector")
    public String sector;
    @SerializedName("Industry")
    public String industry;
    @SerializedName("Exchange")
    public String exchange;
    @SerializedName("Currency")
    public String currency;
    @SerializedName("Country")
    public String country;
    @SerializedName("MarketCapitalization")
    public String marketCapitalization;
    @SerializedName("PERatio")
    public String peRatio;
    @SerializedName("52WeekHigh")
    public String week52High;
    @SerializedName("52WeekLow")
    public String week52Low;
    @SerializedName("DividendYield")
    public String dividendYield;
    @SerializedName("EPS")
    public String eps;
    @SerializedName("AnalystTargetPrice")
    public String analystTargetPrice;
}
