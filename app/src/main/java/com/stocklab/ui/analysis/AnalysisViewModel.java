package com.stocklab.ui.analysis;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.PortfolioRepository;
import com.stocklab.data.repository.ProfileRepository;
import com.stocklab.model.Holding;
import com.stocklab.model.Profile;
import com.stocklab.model.Transaction;
import com.stocklab.util.AnalysisHelper;

import java.util.List;
import java.util.Locale;

public class AnalysisViewModel extends ViewModel {

    private ProfileRepository profileRepo;
    private PortfolioRepository portfolioRepo;
    private long profileId;
    private final MutableLiveData<String> score = new MutableLiveData<>();
    private final MutableLiveData<String> returnPercentText = new MutableLiveData<>();
    private final MutableLiveData<String> transactionsCountText = new MutableLiveData<>();

    public void init(Context context, long profileId) {
        this.profileId = profileId;
        profileRepo = new ProfileRepository(context);
        portfolioRepo = new PortfolioRepository(context);
        refresh(context);
    }

    public void refresh(Context context) {
        if (profileRepo == null || portfolioRepo == null) return;
        Profile p = profileRepo.getProfileByIdSync(profileId);
        if (p == null) return;

        List<Holding> holdings = portfolioRepo.getHoldingsSync(profileId);
        List<Transaction> transactions = portfolioRepo.getTransactionsSync(profileId);

        // Optionally fetch current prices for holdings to compute total value
        if (holdings.isEmpty()) {
            double totalValue = p.currentCash / 100.0;
            double returnPct = AnalysisHelper.computeReturnPercent(totalValue, p.startingCash);
            int sc = AnalysisHelper.computeScore(returnPct, transactions.size());
            score.setValue(String.valueOf(sc));
            returnPercentText.setValue(String.format(Locale.US, "%.2f%% return", returnPct));
            transactionsCountText.setValue(transactions.size() + " transactions");
            return;
        }

        // Simple: use average cost for value if we don't fetch live prices (to avoid many API calls)
        double totalValue = AnalysisHelper.computeTotalPortfolioValue(p.currentCash, holdings);
        double returnPct = AnalysisHelper.computeReturnPercent(totalValue, p.startingCash);
        int sc = AnalysisHelper.computeScore(returnPct, transactions.size());
        score.setValue(String.valueOf(sc));
        returnPercentText.setValue(String.format(Locale.US, "%.2f%% return", returnPct));
        transactionsCountText.setValue(transactions.size() + " transactions");
    }

    public LiveData<String> getScore() { return score; }
    public LiveData<String> getReturnPercentText() { return returnPercentText; }
    public LiveData<String> getTransactionsCountText() { return transactionsCountText; }
}
