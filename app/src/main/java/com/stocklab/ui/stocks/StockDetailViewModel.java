package com.stocklab.ui.stocks;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.PortfolioRepository;
import com.stocklab.data.repository.StockRepository;
import com.stocklab.model.CompanyOverview;
import com.stocklab.model.DailyOhlc;
import com.stocklab.model.StockQuote;

import java.util.List;
import java.util.Locale;

public class StockDetailViewModel extends ViewModel {

    private StockRepository stockRepo;
    private PortfolioRepository portfolioRepo;
    private long profileId;
    private String symbol, name;
    private double currentPrice;
    private double heldQuantity;
    private final MutableLiveData<String> priceText = new MutableLiveData<>();
    private final MutableLiveData<String> changeText = new MutableLiveData<>();
    private final MutableLiveData<String> heldText = new MutableLiveData<>();
    private final MutableLiveData<CompanyOverview> overview = new MutableLiveData<>();
    private final MutableLiveData<String> dailySummaryText = new MutableLiveData<>();

    public void init(Context context, long profileId, String symbol, String name) {
        this.profileId = profileId;
        this.symbol = symbol;
        this.name = name;
        stockRepo = new StockRepository();
        portfolioRepo = new PortfolioRepository(context);

        heldQuantity = getHeldQuantity(context);
        heldText.setValue(String.format(Locale.US, "You hold: %.2f shares", heldQuantity));

        stockRepo.fetchQuote(symbol, new StockRepository.QuoteCallback() {
            @Override
            public void onSuccess(StockQuote quote) {
                currentPrice = quote.price;
                priceText.setValue(String.format(Locale.US, "$%.2f", quote.price));
                String ch = quote.change >= 0 ? "+" : "";
                changeText.setValue(String.format(Locale.US, "%s%.2f (%.2f%%)", ch, quote.change, quote.changePercent));
            }
            @Override
            public void onError(String message) {
                priceText.setValue("—");
                changeText.setValue(message);
            }
        });

        stockRepo.fetchOverview(symbol, new StockRepository.OverviewCallback() {
            @Override
            public void onSuccess(CompanyOverview o) {
                overview.setValue(o);
            }
            @Override
            public void onError(String message) {
                overview.setValue(null);
            }
        });

        stockRepo.fetchTimeSeriesDaily(symbol, new StockRepository.DailySeriesCallback() {
            @Override
            public void onSuccess(List<DailyOhlc> sortedByDateNewestFirst) {
                StringBuilder sb = new StringBuilder();
                int n = Math.min(5, sortedByDateNewestFirst.size());
                for (int i = 0; i < n; i++) {
                    DailyOhlc d = sortedByDateNewestFirst.get(i);
                    sb.append(d.date).append(" O:").append(String.format(Locale.US, "%.2f", d.open))
                      .append(" H:").append(String.format(Locale.US, "%.2f", d.high))
                      .append(" L:").append(String.format(Locale.US, "%.2f", d.low))
                      .append(" C:").append(String.format(Locale.US, "%.2f", d.close));
                    if (i < n - 1) sb.append("\n");
                }
                dailySummaryText.setValue(sb.length() > 0 ? sb.toString() : null);
            }
            @Override
            public void onError(String message) {
                dailySummaryText.setValue(null);
            }
        });
    }

    private double getHeldQuantity(Context context) {
        if (portfolioRepo == null) return 0;
        java.util.List<com.stocklab.model.Holding> list = portfolioRepo.getHoldingsSync(profileId);
        for (com.stocklab.model.Holding h : list) {
            if (symbol.equals(h.symbol)) return h.quantity;
        }
        return 0;
    }

    public void buy(double quantity, StockDetailViewModel.Callback callback) {
        if (portfolioRepo == null || currentPrice <= 0) {
            if (callback != null) callback.onResult(false);
            return;
        }
        boolean ok = portfolioRepo.buy(profileId, symbol, name, quantity, currentPrice);
        if (callback != null) callback.onResult(ok);
    }

    public void sell(double quantity, StockDetailViewModel.Callback callback) {
        if (portfolioRepo == null || quantity > heldQuantity) {
            if (callback != null) callback.onResult(false);
            return;
        }
        boolean ok = portfolioRepo.sell(profileId, symbol, name, quantity, currentPrice > 0 ? currentPrice : 0);
        if (callback != null) callback.onResult(ok);
    }

    public interface Callback { void onResult(boolean success); }

    public MutableLiveData<String> getPriceText() { return priceText; }
    public MutableLiveData<String> getChangeText() { return changeText; }
    public MutableLiveData<String> getHeldText() { return heldText; }
    public MutableLiveData<CompanyOverview> getOverview() { return overview; }
    public MutableLiveData<String> getDailySummaryText() { return dailySummaryText; }
}
