package com.stocklab.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.stocklab.data.remote.StockApi;
import com.stocklab.data.remote.StockApiClient;
import com.stocklab.data.remote.dto.GlobalQuoteDto;
import com.stocklab.data.remote.dto.SymbolSearchDto;
import com.stocklab.model.StockQuote;
import com.stocklab.model.StockSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for real-time stock data from API.
 */
public class StockRepository {

    private final StockApi api;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public StockRepository() {
        this.api = StockApiClient.getApi();
    }

    public interface QuoteCallback {
        void onSuccess(StockQuote quote);
        void onError(String message);
    }

    public void fetchQuote(String symbol, QuoteCallback callback) {
        String key = StockApiClient.getApiKey();
        api.getGlobalQuote(symbol, key).enqueue(new Callback<GlobalQuoteDto>() {
            @Override
            public void onResponse(Call<GlobalQuoteDto> call, Response<GlobalQuoteDto> response) {
                if (response.isSuccessful() && response.body() != null && response.body().globalQuote != null) {
                    GlobalQuoteDto.GlobalQuote q = response.body().globalQuote;
                    double price = parseDouble(q.price);
                    double open = parseDouble(q.open);
                    double high = parseDouble(q.high);
                    double low = parseDouble(q.low);
                    double prev = parseDouble(q.previousClose);
                    double change = parseDouble(q.change);
                    double changePct = parseDouble(q.changePercent);
                    StockQuote quote = new StockQuote(q.symbol, price, open, high, low, prev, change, changePct, q.latestTradingDay);
                    runOnMain(() -> callback.onSuccess(quote));
                } else {
                    runOnMain(() -> callback.onError("No quote data"));
                }
            }

            @Override
            public void onFailure(Call<GlobalQuoteDto> call, Throwable t) {
                runOnMain(() -> callback.onError(t != null ? t.getMessage() : "Network error"));
            }
        });
    }

    public interface SearchCallback {
        void onSuccess(List<StockSearchResult> results);
        void onError(String message);
    }

    public void searchSymbols(String keywords, SearchCallback callback) {
        String key = StockApiClient.getApiKey();
        api.symbolSearch(keywords, key).enqueue(new Callback<SymbolSearchDto>() {
            @Override
            public void onResponse(Call<SymbolSearchDto> call, Response<SymbolSearchDto> response) {
                if (response.isSuccessful() && response.body() != null && response.body().bestMatches != null) {
                    List<StockSearchResult> list = new ArrayList<>();
                    for (SymbolSearchDto.Match m : response.body().bestMatches) {
                        if (m.symbol != null && m.name != null) {
                            list.add(new StockSearchResult(
                                m.symbol, m.name,
                                m.type != null ? m.type : "",
                                m.region != null ? m.region : "",
                                m.currency != null ? m.currency : ""
                            ));
                        }
                    }
                    runOnMain(() -> callback.onSuccess(list));
                } else {
                    runOnMain(() -> callback.onSuccess(new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<SymbolSearchDto> call, Throwable t) {
                runOnMain(() -> callback.onError(t != null ? t.getMessage() : "Network error"));
            }
        });
    }

    private static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void runOnMain(Runnable r) {
        mainHandler.post(r);
    }
}
