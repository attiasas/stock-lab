package com.stocklab.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Alpha Vantage TIME_SERIES_DAILY (compact = last 100 points).
 * https://www.alphavantage.co/documentation/#daily
 */
public class TimeSeriesDailyDto {

    @SerializedName("Time Series (Daily)")
    public Map<String, DayPoint> timeSeries;

    public static class DayPoint {
        @SerializedName("1. open")
        public String open;
        @SerializedName("2. high")
        public String high;
        @SerializedName("3. low")
        public String low;
        @SerializedName("4. close")
        public String close;
        @SerializedName("5. volume")
        public String volume;
    }
}
