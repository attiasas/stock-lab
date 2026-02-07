package com.stocklab.data.remote;

import com.stocklab.data.remote.dto.CompanyOverviewDto;
import com.stocklab.data.remote.dto.GlobalQuoteDto;
import com.stocklab.data.remote.dto.SymbolSearchDto;
import com.stocklab.data.remote.dto.TimeSeriesDailyDto;
import com.stocklab.data.remote.dto.TopGainersLosersDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Alpha Vantage API (free tier).
 * https://www.alphavantage.co/documentation/
 */
public interface StockApi {

    @GET("query?function=GLOBAL_QUOTE")
    Call<GlobalQuoteDto> getGlobalQuote(@Query("symbol") String symbol, @Query("apikey") String apiKey);

    @GET("query?function=SYMBOL_SEARCH")
    Call<SymbolSearchDto> symbolSearch(@Query("keywords") String keywords, @Query("apikey") String apiKey);

    @GET("query?function=OVERVIEW")
    Call<CompanyOverviewDto> getOverview(@Query("symbol") String symbol, @Query("apikey") String apiKey);

    @GET("query?function=TIME_SERIES_DAILY")
    Call<TimeSeriesDailyDto> getTimeSeriesDaily(@Query("symbol") String symbol, @Query("apikey") String apiKey);

    @GET("query?function=TOP_GAINERS_LOSERS")
    Call<TopGainersLosersDto> getTopGainersLosers(@Query("apikey") String apiKey);
}
