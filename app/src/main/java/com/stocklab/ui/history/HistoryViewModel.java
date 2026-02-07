package com.stocklab.ui.history;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.PortfolioRepository;
import com.stocklab.model.Transaction;

import java.util.List;

public class HistoryViewModel extends ViewModel {

    private PortfolioRepository repository;
    private LiveData<List<Transaction>> transactions;

    public void init(Context context, long profileId) {
        if (repository == null) {
            repository = new PortfolioRepository(context);
        }
        transactions = repository.getTransactions(profileId);
    }

    public void refresh() {
        // LiveData will update automatically when DB changes
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }
}
