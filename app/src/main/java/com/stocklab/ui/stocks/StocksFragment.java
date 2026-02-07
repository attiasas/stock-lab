package com.stocklab.ui.stocks;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stocklab.R;
import com.stocklab.ui.main.MainActivity;
import com.stocklab.model.StockSearchResult;

import java.util.ArrayList;
import java.util.List;

public class StocksFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";

    private long profileId;
    private StockSearchViewModel viewModel;
    private StockSearchAdapter adapter;

    public static StocksFragment newInstance(long profileId) {
        StocksFragment f = new StocksFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_PROFILE_ID, profileId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileId = getArguments() != null ? getArguments().getLong(ARG_PROFILE_ID, -1) : -1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(StockSearchViewModel.class);

        RecyclerView recycler = view.findViewById(R.id.recycler_stocks);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StockSearchAdapter(new ArrayList<>(), this::onStockSelected);
        recycler.setAdapter(adapter);

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null) adapter.setItems(results);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        TextInputEditText inputSearch = view.findViewById(R.id.input_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s != null ? s.toString().trim() : "";
                if (q.length() >= 2) viewModel.search(q);
                else adapter.setItems(new ArrayList<>());
            }
        });

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(() -> {
            if (inputSearch.getText() != null) {
                String q = inputSearch.getText().toString().trim();
                if (q.length() >= 2) viewModel.search(q);
            }
            swipe.setRefreshing(false);
        });
    }

    private void onStockSelected(StockSearchResult result) {
        MainActivity activity = (MainActivity) requireActivity();
        activity.loadFragmentWithBackStack(StockDetailFragment.newInstance(profileId, result.symbol, result.name));
    }

    public static class StockSearchAdapter extends RecyclerView.Adapter<StockSearchAdapter.VH> {
        private List<StockSearchResult> items = new ArrayList<>();
        private final OnItemClickListener onItemClick;

        interface OnItemClickListener { void onClick(StockSearchResult r); }

        StockSearchAdapter(List<StockSearchResult> items, OnItemClickListener onItemClick) {
            this.items = items;
            this.onItemClick = onItemClick;
        }

        void setItems(List<StockSearchResult> items) {
            this.items = items != null ? items : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_search, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            StockSearchResult r = items.get(position);
            holder.symbol.setText(r.symbol);
            holder.name.setText(r.name);
            holder.region.setText(r.region != null && !r.region.isEmpty() ? r.region : r.currency);
            holder.itemView.setOnClickListener(v -> onItemClick.onClick(r));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final android.widget.TextView symbol, name, region;

            VH(View itemView) {
                super(itemView);
                symbol = itemView.findViewById(R.id.stock_symbol);
                name = itemView.findViewById(R.id.stock_name);
                region = itemView.findViewById(R.id.stock_region);
            }
        }
    }
}
