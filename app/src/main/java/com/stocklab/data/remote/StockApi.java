package com.stocklab.data.remote;

import com.stocklab.data.remote.dto.GlobalQuoteDto;
import com.stocklab.data.remote.dto.SymbolSearchDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Alpha Vantage API for real-time stock quotes and symbol search.
 * Get a free API key at https://www.alphavantage.co/support/#api-key
 * and set STOCK_API_KEY in local.properties or BuildConfig.
 */
public interface StockApi {

    @GET("query?function=GLOBAL_QUOTE")
    Call<GlobalQuoteDto> getGlobalQuote(@Query("symbol") String symbol, @Query("apikey") String apiKey);

    @GET("query?function=SYMBOL_SEARCH")
    Call<SymbolSearchDto> symbolSearch(@Query("keywords") String keywords, @Query("apikey") String apiKey);
}
