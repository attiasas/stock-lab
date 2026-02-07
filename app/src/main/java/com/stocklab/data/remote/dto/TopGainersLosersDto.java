package com.stocklab.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Alpha Vantage TOP_GAINERS_LOSERS (US market).
 * https://www.alphavantage.co/documentation/#top-gainers-losers
 */
public class TopGainersLosersDto {

    @SerializedName("top_gainers")
    public List<Mover> topGainers;
    @SerializedName("top_losers")
    public List<Mover> topLosers;
    @SerializedName("most_actively_traded")
    public List<Mover> mostActivelyTraded;

    public static class Mover {
        @SerializedName("ticker")
        public String ticker;
        @SerializedName("price")
        public String price;
        @SerializedName("change_amount")
        public String changeAmount;
        @SerializedName("change_percentage")
        public String changePercentage;
        @SerializedName("volume")
        public String volume;
    }
}
