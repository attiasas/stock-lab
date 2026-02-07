package com.stocklab.ui.stocks;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.StockRepository;

import java.util.List;

import com.stocklab.model.StockSearchResult;

public class StockSearchViewModel extends ViewModel {

    private final StockRepository stockRepo = new StockRepository();
    private final MutableLiveData<List<StockSearchResult>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void search(String keywords) {
        error.setValue(null);
        stockRepo.searchSymbols(keywords, new StockRepository.SearchCallback() {
            @Override
            public void onSuccess(List<StockSearchResult> results) {
                searchResults.setValue(results);
            }
            @Override
            public void onError(String message) {
                error.setValue(message);
                searchResults.setValue(null);
            }
        });
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResults() { return searchResults; }
    public MutableLiveData<String> getError() { return error; }
}
