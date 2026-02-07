package com.stocklab.ui.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stocklab.R;
import com.stocklab.model.Holding;
import com.stocklab.ui.main.MainActivity;
import com.stocklab.ui.stocks.StockDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PortfolioFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";

    private long profileId;
    private PortfolioViewModel viewModel;
    private HoldingsAdapter adapter;

    public static PortfolioFragment newInstance(long profileId) {
        PortfolioFragment f = new PortfolioFragment();
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
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PortfolioViewModel.class);
        viewModel.init(requireContext(), profileId);

        TextView balanceValue = view.findViewById(R.id.balance_value);
        TextView totalValue = view.findViewById(R.id.total_value);
        viewModel.getBalanceText().observe(getViewLifecycleOwner(), balanceValue::setText);
        viewModel.getTotalValueText().observe(getViewLifecycleOwner(), totalValue::setText);

        RecyclerView recycler = view.findViewById(R.id.recycler_holdings);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HoldingsAdapter(new ArrayList<>(), requireContext(), profileId, this::openStockDetail);
        recycler.setAdapter(adapter);

        viewModel.getHoldings().observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list != null ? list : new ArrayList<>());
            viewModel.refreshTotals(requireContext(), profileId);
        });

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(() -> {
            viewModel.refreshTotals(requireContext(), profileId);
            swipe.setRefreshing(false);
        });
    }

    private void openStockDetail(String symbol, String name) {
        MainActivity activity = (MainActivity) requireActivity();
        activity.loadFragmentWithBackStack(StockDetailFragment.newInstance(profileId, symbol, name));
    }

    public static class HoldingsAdapter extends RecyclerView.Adapter<HoldingsAdapter.VH> {
        private List<Holding> items = new ArrayList<>();
        private final android.content.Context context;
        private final long profileId;
        private final OnItemClickListener onItemClick;

        interface OnItemClickListener { void onClick(String symbol, String name); }

        HoldingsAdapter(List<Holding> items, android.content.Context context, long profileId, OnItemClickListener onItemClick) {
            this.items = items;
            this.context = context;
            this.profileId = profileId;
            this.onItemClick = onItemClick;
        }

        void setItems(List<Holding> items) {
            this.items = items != null ? items : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holding, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Holding h = items.get(position);
            holder.symbol.setText(h.symbol);
            holder.name.setText(h.name);
            double value = h.quantity * (h.currentPrice > 0 ? h.currentPrice : h.averageCostPerShare);
            holder.value.setText(String.format(Locale.US, "$%.2f", value));
            holder.qtyCost.setText(String.format(Locale.US, "%.2f shares · Avg $%.2f", h.quantity, h.averageCostPerShare));
            holder.itemView.setOnClickListener(v -> onItemClick.onClick(h.symbol, h.name));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView symbol, name, value, qtyCost;

            VH(View itemView) {
                super(itemView);
                symbol = itemView.findViewById(R.id.holding_symbol);
                name = itemView.findViewById(R.id.holding_name);
                value = itemView.findViewById(R.id.holding_value);
                qtyCost = itemView.findViewById(R.id.holding_qty_cost);
            }
        }
    }
}
