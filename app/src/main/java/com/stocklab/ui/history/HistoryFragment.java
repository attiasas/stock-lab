package com.stocklab.ui.history;

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
import com.stocklab.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";

    private long profileId;
    private HistoryViewModel viewModel;
    private TransactionAdapter adapter;

    public static HistoryFragment newInstance(long profileId) {
        HistoryFragment f = new HistoryFragment();
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        viewModel.init(requireContext(), profileId);

        RecyclerView recycler = view.findViewById(R.id.recycler_history);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        viewModel.getTransactions().observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list != null ? list : new ArrayList<>());
        });

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(() -> {
            viewModel.refresh();
            swipe.setRefreshing(false);
        });
    }

    public static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.US);

        private List<Transaction> items = new ArrayList<>();

        TransactionAdapter(List<Transaction> items) {
            this.items = items;
        }

        void setItems(List<Transaction> items) {
            this.items = items != null ? items : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Transaction t = items.get(position);
            holder.typeSymbol.setText(t.type.toUpperCase() + " " + t.symbol);
            holder.date.setText(DATE_FORMAT.format(new Date(t.timestamp)));
            holder.amount.setText(String.format(Locale.US, "%.2f @ $%.2f", t.quantity, t.pricePerShare));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView typeSymbol, date, amount;

            VH(View itemView) {
                super(itemView);
                typeSymbol = itemView.findViewById(R.id.tx_type_symbol);
                date = itemView.findViewById(R.id.tx_date);
                amount = itemView.findViewById(R.id.tx_amount);
            }
        }
    }
}
