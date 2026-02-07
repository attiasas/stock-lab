package com.stocklab.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.stocklab.data.remote.StockApi;
import com.stocklab.data.remote.StockApiClient;
import com.stocklab.data.remote.dto.CompanyOverviewDto;
import com.stocklab.data.remote.dto.GlobalQuoteDto;
import com.stocklab.data.remote.dto.SymbolSearchDto;
import com.stocklab.data.remote.dto.TimeSeriesDailyDto;
import com.stocklab.data.remote.dto.TopGainersLosersDto;
import com.stocklab.model.CompanyOverview;
import com.stocklab.model.DailyOhlc;
import com.stocklab.model.StockQuote;
import com.stocklab.model.StockSearchResult;
import com.stocklab.model.TopMover;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public interface OverviewCallback {
        void onSuccess(CompanyOverview overview);
        void onError(String message);
    }

    public void fetchOverview(String symbol, OverviewCallback callback) {
        String key = StockApiClient.getApiKey();
        api.getOverview(symbol, key).enqueue(new Callback<CompanyOverviewDto>() {
            @Override
            public void onResponse(Call<CompanyOverviewDto> call, Response<CompanyOverviewDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CompanyOverviewDto dto = response.body();
                    CompanyOverview o = mapOverview(dto);
                    runOnMain(() -> callback.onSuccess(o));
                } else {
                    runOnMain(() -> callback.onError("No overview data"));
                }
            }
            @Override
            public void onFailure(Call<CompanyOverviewDto> call, Throwable t) {
                runOnMain(() -> callback.onError(t != null ? t.getMessage() : "Network error"));
            }
        });
    }

    public interface DailySeriesCallback {
        void onSuccess(List<DailyOhlc> sortedByDateNewestFirst);
        void onError(String message);
    }

    public void fetchTimeSeriesDaily(String symbol, DailySeriesCallback callback) {
        String key = StockApiClient.getApiKey();
        api.getTimeSeriesDaily(symbol, key).enqueue(new Callback<TimeSeriesDailyDto>() {
            @Override
            public void onResponse(Call<TimeSeriesDailyDto> call, Response<TimeSeriesDailyDto> response) {
                if (response.isSuccessful() && response.body() != null && response.body().timeSeries != null) {
                    List<DailyOhlc> list = new ArrayList<>();
                    for (Map.Entry<String, TimeSeriesDailyDto.DayPoint> e : response.body().timeSeries.entrySet()) {
                        TimeSeriesDailyDto.DayPoint p = e.getValue();
                        if (p != null) {
                            list.add(new DailyOhlc(
                                e.getKey(),
                                parseDouble(p.open),
                                parseDouble(p.high),
                                parseDouble(p.low),
                                parseDouble(p.close),
                                parseLong(p.volume)
                            ));
                        }
                    }
                    list.sort((a, b) -> b.date.compareTo(a.date));
                    runOnMain(() -> callback.onSuccess(list));
                } else {
                    runOnMain(() -> callback.onSuccess(new ArrayList<>()));
                }
            }
            @Override
            public void onFailure(Call<TimeSeriesDailyDto> call, Throwable t) {
                runOnMain(() -> callback.onError(t != null ? t.getMessage() : "Network error"));
            }
        });
    }

    public interface TopMoversCallback {
        void onSuccess(List<TopMover> gainers, List<TopMover> losers, List<TopMover> mostActive);
        void onError(String message);
    }

    public void fetchTopGainersLosers(TopMoversCallback callback) {
        String key = StockApiClient.getApiKey();
        api.getTopGainersLosers(key).enqueue(new Callback<TopGainersLosersDto>() {
            @Override
            public void onResponse(Call<TopGainersLosersDto> call, Response<TopGainersLosersDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TopGainersLosersDto dto = response.body();
                    List<TopMover> gainers = mapMovers(dto.topGainers);
                    List<TopMover> losers = mapMovers(dto.topLosers);
                    List<TopMover> active = mapMovers(dto.mostActivelyTraded);
                    runOnMain(() -> callback.onSuccess(gainers, losers, active));
                } else {
                    runOnMain(() -> callback.onError("No data"));
                }
            }
            @Override
            public void onFailure(Call<TopGainersLosersDto> call, Throwable t) {
                runOnMain(() -> callback.onError(t != null ? t.getMessage() : "Network error"));
            }
        });
    }

    private static CompanyOverview mapOverview(CompanyOverviewDto d) {
        return new CompanyOverview(
            str(d.symbol),
            str(d.name),
            str(d.description),
            str(d.sector),
            str(d.industry),
            str(d.exchange),
            str(d.currency),
            str(d.country),
            formatMarketCap(d.marketCapitalization),
            str(d.peRatio),
            str(d.week52High),
            str(d.week52Low),
            str(d.dividendYield),
            str(d.eps),
            str(d.analystTargetPrice)
        );
    }

    private static String str(String s) {
        return s != null ? s.trim() : "";
    }

    private static String formatMarketCap(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        try {
            long v = Long.parseLong(s.trim());
            if (v >= 1_000_000_000_000L) return String.format("%.2fT", v / 1_000_000_000_000.0);
            if (v >= 1_000_000_000) return String.format("%.2fB", v / 1_000_000_000.0);
            if (v >= 1_000_000) return String.format("%.2fM", v / 1_000_000.0);
            return String.valueOf(v);
        } catch (NumberFormatException e) {
            return s;
        }
    }

    private static List<TopMover> mapMovers(List<TopGainersLosersDto.Mover> list) {
        List<TopMover> out = new ArrayList<>();
        if (list == null) return out;
        for (TopGainersLosersDto.Mover m : list) {
            if (m != null && m.ticker != null) {
                out.add(new TopMover(
                    m.ticker,
                    parseDouble(m.price),
                    parseDouble(m.changeAmount),
                    parseDouble(m.changePercentage),
                    parseLong(m.volume)
                ));
            }
        }
        return out;
    }

    private static long parseLong(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            return Long.parseLong(s.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
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
