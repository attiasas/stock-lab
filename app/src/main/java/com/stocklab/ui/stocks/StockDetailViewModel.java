package com.stocklab.ui.stocks;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.PortfolioRepository;
import com.stocklab.data.repository.StockRepository;
import com.stocklab.model.StockQuote;

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
}
