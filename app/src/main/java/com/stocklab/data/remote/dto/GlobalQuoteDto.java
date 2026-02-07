package com.stocklab.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Alpha Vantage GLOBAL_QUOTE response wrapper.
 */
public class GlobalQuoteDto {

    @SerializedName("Global Quote")
    public GlobalQuote globalQuote;

    public static class GlobalQuote {
        @SerializedName("01. symbol")
        public String symbol;
        @SerializedName("02. open")
        public String open;
        @SerializedName("03. high")
        public String high;
        @SerializedName("04. low")
        public String low;
        @SerializedName("05. price")
        public String price;
        @SerializedName("06. volume")
        public String volume;
        @SerializedName("07. latest trading day")
        public String latestTradingDay;
        @SerializedName("08. previous close")
        public String previousClose;
        @SerializedName("09. change")
        public String change;
        @SerializedName("10. change percent")
        public String changePercent;
    }
}
