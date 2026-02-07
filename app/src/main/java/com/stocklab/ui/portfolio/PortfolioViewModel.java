package com.stocklab.ui.portfolio;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.PortfolioRepository;
import com.stocklab.data.repository.ProfileRepository;
import com.stocklab.model.Holding;
import com.stocklab.model.Profile;
import com.stocklab.util.AnalysisHelper;

import java.util.List;
import java.util.Locale;

public class PortfolioViewModel extends ViewModel {

    private ProfileRepository profileRepo;
    private PortfolioRepository portfolioRepo;
    private final MutableLiveData<String> balanceText = new MutableLiveData<>();
    private final MutableLiveData<String> totalValueText = new MutableLiveData<>();
    private LiveData<List<Holding>> holdings;
    private String currency = "USD";
    private String currencySymbol = "$";

    public void init(Context context, long profileId) {
        if (profileRepo == null) {
            profileRepo = new ProfileRepository(context);
            portfolioRepo = new PortfolioRepository(context);
        }
        Profile p = profileRepo.getProfileByIdSync(profileId);
        if (p != null) {
            currency = p.currency;
            currencySymbol = "USD".equals(currency) ? "$" : currency + " ";
        }
        holdings = portfolioRepo.getHoldings(profileId);
        refreshTotals(context, profileId);
    }

    public void refreshTotals(Context context, long profileId) {
        if (profileRepo == null || portfolioRepo == null) return;
        Profile p = profileRepo.getProfileByIdSync(profileId);
        if (p == null) return;
        List<Holding> list = portfolioRepo.getHoldingsSync(profileId);
        double total = AnalysisHelper.computeTotalPortfolioValue(p.currentCash, list);
        balanceText.setValue(String.format(Locale.US, "%s%.2f", currencySymbol, p.currentCash / 100.0));
        totalValueText.setValue(String.format(Locale.US, "%s%.2f", currencySymbol, total));
    }

    public LiveData<String> getBalanceText() { return balanceText; }
    public LiveData<String> getTotalValueText() { return totalValueText; }
    public LiveData<List<Holding>> getHoldings() { return holdings; }
}
