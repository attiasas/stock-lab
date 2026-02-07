package com.stocklab.ui.stocks;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.stocklab.model.TopMover;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StocksFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";

    private long profileId;
    private StockSearchViewModel viewModel;
    private StockSearchAdapter adapter;
    private TopMoversAdapter moversAdapter;
    private View sectionMovers;

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

        sectionMovers = view.findViewById(R.id.section_movers);
        RecyclerView recyclerMovers = view.findViewById(R.id.recycler_movers);
        recyclerMovers.setLayoutManager(new LinearLayoutManager(requireContext()));
        moversAdapter = new TopMoversAdapter(new ArrayList<>(), this::onMoverSelected);
        recyclerMovers.setAdapter(moversAdapter);

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null) adapter.setItems(results);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.getTopGainers().observe(getViewLifecycleOwner(), g -> buildMoversList());
        viewModel.getTopLosers().observe(getViewLifecycleOwner(), l -> buildMoversList());
        viewModel.getMostActivelyTraded().observe(getViewLifecycleOwner(), m -> buildMoversList());

        viewModel.loadTopMovers();

        TextInputEditText inputSearch = view.findViewById(R.id.input_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s != null ? s.toString().trim() : "";
                if (q.length() >= 2) {
                    viewModel.search(q);
                    sectionMovers.setVisibility(View.GONE);
                } else {
                    adapter.setItems(new ArrayList<>());
                    sectionMovers.setVisibility(View.VISIBLE);
                }
            }
        });

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(() -> {
            TextInputEditText input = view.findViewById(R.id.input_search);
            if (input.getText() != null) {
                String q = input.getText().toString().trim();
                if (q.length() >= 2) viewModel.search(q);
                else viewModel.loadTopMovers();
            }
            swipe.setRefreshing(false);
        });
    }

    private void buildMoversList() {
        List<TopMover> g = viewModel.getTopGainers().getValue();
        List<TopMover> l = viewModel.getTopLosers().getValue();
        List<TopMover> m = viewModel.getMostActivelyTraded().getValue();
        if (g == null) g = new ArrayList<>();
        if (l == null) l = new ArrayList<>();
        if (m == null) m = new ArrayList<>();
        List<Object> items = new ArrayList<>();
        if (!g.isEmpty()) {
            items.add("Top Gainers");
            items.addAll(g);
        }
        if (!l.isEmpty()) {
            items.add("Top Losers");
            items.addAll(l);
        }
        if (!m.isEmpty()) {
            items.add("Most Active");
            items.addAll(m);
        }
        moversAdapter.setItems(items);
    }

    private void onMoverSelected(TopMover mover) {
        MainActivity activity = (MainActivity) requireActivity();
        activity.loadFragmentWithBackStack(StockDetailFragment.newInstance(profileId, mover.ticker, ""));
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
            final TextView symbol, name, region;

            VH(View itemView) {
                super(itemView);
                symbol = itemView.findViewById(R.id.stock_symbol);
                name = itemView.findViewById(R.id.stock_name);
                region = itemView.findViewById(R.id.stock_region);
            }
        }
    }

    /** Adapter for Top Movers list: headers (String) and mover rows (TopMover). */
    static class TopMoversAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_MOVER = 1;

        private List<Object> items = new ArrayList<>();
        private final OnMoverClickListener onMoverClick;

        interface OnMoverClickListener { void onClick(TopMover m); }

        TopMoversAdapter(List<Object> items, OnMoverClickListener onMoverClick) {
            this.items = items;
            this.onMoverClick = onMoverClick;
        }

        void setItems(List<Object> items) {
            this.items = items != null ? items : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position) instanceof String ? TYPE_HEADER : TYPE_MOVER;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mover_header, parent, false);
                return new HeaderVH(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_mover, parent, false);
                return new MoverVH(v);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Object item = items.get(position);
            if (holder instanceof HeaderVH) {
                ((HeaderVH) holder).title.setText((String) item);
            } else if (holder instanceof MoverVH) {
                TopMover m = (TopMover) item;
                MoverVH h = (MoverVH) holder;
                h.ticker.setText(m.ticker);
                h.price.setText(String.format(Locale.US, "$%.2f", m.price));
                String ch = m.changePercentage >= 0 ? "+" : "";
                h.change.setText(String.format(Locale.US, "%s%.2f%%", ch, m.changePercentage));
                h.change.setTextColor(h.change.getContext().getColor(
                    m.changePercentage >= 0 ? com.stocklab.R.color.accent : com.stocklab.R.color.accent_negative));
                h.itemView.setOnClickListener(v -> onMoverClick.onClick(m));
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class HeaderVH extends RecyclerView.ViewHolder {
            final TextView title;
            HeaderVH(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.header_title);
            }
        }

        static class MoverVH extends RecyclerView.ViewHolder {
            final TextView ticker, price, change;
            MoverVH(View itemView) {
                super(itemView);
                ticker = itemView.findViewById(R.id.mover_ticker);
                price = itemView.findViewById(R.id.mover_price);
                change = itemView.findViewById(R.id.mover_change);
            }
        }
    }
}
