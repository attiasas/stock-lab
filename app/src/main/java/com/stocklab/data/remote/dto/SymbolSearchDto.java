package com.stocklab.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Alpha Vantage SYMBOL_SEARCH response.
 */
public class SymbolSearchDto {

    @SerializedName("bestMatches")
    public List<Match> bestMatches;

    public static class Match {
        @SerializedName("1. symbol")
        public String symbol;
        @SerializedName("2. name")
        public String name;
        @SerializedName("3. type")
        public String type;
        @SerializedName("4. region")
        public String region;
        @SerializedName("8. currency")
        public String currency;
    }
}
