package com.stocklab.ui.stocks;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stocklab.data.repository.StockRepository;
import com.stocklab.model.StockSearchResult;
import com.stocklab.model.TopMover;

import java.util.ArrayList;
import java.util.List;

public class StockSearchViewModel extends ViewModel {

    private final StockRepository stockRepo = new StockRepository();
    private final MutableLiveData<List<StockSearchResult>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<TopMover>> topGainers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<TopMover>> topLosers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<TopMover>> mostActivelyTraded = new MutableLiveData<>(new ArrayList<>());

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

    public void loadTopMovers() {
        stockRepo.fetchTopGainersLosers(new StockRepository.TopMoversCallback() {
            @Override
            public void onSuccess(List<TopMover> gainers, List<TopMover> losers, List<TopMover> mostActive) {
                topGainers.setValue(gainers != null ? gainers : new ArrayList<>());
                topLosers.setValue(losers != null ? losers : new ArrayList<>());
                mostActivelyTraded.setValue(mostActive != null ? mostActive : new ArrayList<>());
            }
            @Override
            public void onError(String message) {
                topGainers.setValue(new ArrayList<>());
                topLosers.setValue(new ArrayList<>());
                mostActivelyTraded.setValue(new ArrayList<>());
            }
        });
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResults() { return searchResults; }
    public MutableLiveData<String> getError() { return error; }
    public MutableLiveData<List<TopMover>> getTopGainers() { return topGainers; }
    public MutableLiveData<List<TopMover>> getTopLosers() { return topLosers; }
    public MutableLiveData<List<TopMover>> getMostActivelyTraded() { return mostActivelyTraded; }
}
