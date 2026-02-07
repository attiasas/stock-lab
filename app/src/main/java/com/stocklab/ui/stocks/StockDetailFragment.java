package com.stocklab.ui.stocks;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.stocklab.R;
import com.stocklab.model.CompanyOverview;
import com.stocklab.ui.main.MainActivity;

public class StockDetailFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";
    private static final String ARG_SYMBOL = "symbol";
    private static final String ARG_NAME = "name";

    private long profileId;
    private String symbol, name;
    private StockDetailViewModel viewModel;
    private TextInputEditText inputQuantity;
    private TextView detailPrice, detailChange, detailHeld;

    public static StockDetailFragment newInstance(long profileId, String symbol, String name) {
        StockDetailFragment f = new StockDetailFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_PROFILE_ID, profileId);
        b.putString(ARG_SYMBOL, symbol);
        b.putString(ARG_NAME, name != null ? name : "");
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            profileId = args.getLong(ARG_PROFILE_ID, -1);
            symbol = args.getString(ARG_SYMBOL, "");
            name = args.getString(ARG_NAME, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarTitle(symbol);
        }
        viewModel = new ViewModelProvider(this).get(StockDetailViewModel.class);
        viewModel.init(requireContext(), profileId, symbol, name);

        TextView detailSymbol = view.findViewById(R.id.detail_symbol);
        TextView detailName = view.findViewById(R.id.detail_name);
        detailPrice = view.findViewById(R.id.detail_price);
        detailChange = view.findViewById(R.id.detail_change);
        detailHeld = view.findViewById(R.id.detail_held);
        inputQuantity = view.findViewById(R.id.input_quantity);
        MaterialButton btnBuy = view.findViewById(R.id.btn_buy);
        MaterialButton btnSell = view.findViewById(R.id.btn_sell);

        detailSymbol.setText(symbol);
        detailName.setText(name);

        viewModel.getPriceText().observe(getViewLifecycleOwner(), detailPrice::setText);
        viewModel.getChangeText().observe(getViewLifecycleOwner(), detailChange::setText);
        viewModel.getHeldText().observe(getViewLifecycleOwner(), detailHeld::setText);

        TextView detailSectorIndustry = view.findViewById(R.id.detail_sector_industry);
        TextView detailMarketCapPe = view.findViewById(R.id.detail_market_cap_pe);
        TextView detail52w = view.findViewById(R.id.detail_52w);
        TextView detailDescription = view.findViewById(R.id.detail_description);
        TextView labelDaily = view.findViewById(R.id.label_daily);
        TextView detailDailySummary = view.findViewById(R.id.detail_daily_summary);

        viewModel.getOverview().observe(getViewLifecycleOwner(), o -> {
            if (o == null) {
                detailSectorIndustry.setVisibility(View.GONE);
                detailMarketCapPe.setVisibility(View.GONE);
                detail52w.setVisibility(View.GONE);
                detailDescription.setVisibility(View.GONE);
                return;
            }
            if (!TextUtils.isEmpty(o.sector) || !TextUtils.isEmpty(o.industry)) {
                detailSectorIndustry.setText(buildSectorIndustry(o));
                detailSectorIndustry.setVisibility(View.VISIBLE);
            } else {
                detailSectorIndustry.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(o.marketCap) || !TextUtils.isEmpty(o.peRatio)) {
                detailMarketCapPe.setText(buildMarketCapPe(o));
                detailMarketCapPe.setVisibility(View.VISIBLE);
            } else {
                detailMarketCapPe.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(o.week52High) || !TextUtils.isEmpty(o.week52Low)) {
                detail52w.setText("52W: " + o.week52High + " - " + o.week52Low);
                detail52w.setVisibility(View.VISIBLE);
            } else {
                detail52w.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(o.description)) {
                detailDescription.setText(o.description);
                detailDescription.setVisibility(View.VISIBLE);
            } else {
                detailDescription.setVisibility(View.GONE);
            }
        });

        viewModel.getDailySummaryText().observe(getViewLifecycleOwner(), text -> {
            if (text == null || text.isEmpty()) {
                labelDaily.setVisibility(View.GONE);
                detailDailySummary.setVisibility(View.GONE);
            } else {
                labelDaily.setVisibility(View.VISIBLE);
                detailDailySummary.setVisibility(View.VISIBLE);
                detailDailySummary.setText(text);
            }
        });

        btnBuy.setOnClickListener(v -> doBuy());
        btnSell.setOnClickListener(v -> doSell());
    }

    private void doBuy() {
        double qty = parseQuantity();
        if (qty <= 0) {
            Toast.makeText(requireContext(), "Enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.buy(qty, success -> {
            if (success) {
                Toast.makeText(requireContext(), "Buy order placed (simulated)", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) getActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), "Not enough balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doSell() {
        double qty = parseQuantity();
        if (qty <= 0) {
            Toast.makeText(requireContext(), "Enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.sell(qty, success -> {
            if (success) {
                Toast.makeText(requireContext(), "Sell order placed (simulated)", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) getActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double parseQuantity() {
        if (inputQuantity.getText() == null) return 0;
        try {
            return Double.parseDouble(inputQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String buildSectorIndustry(CompanyOverview o) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(o.sector)) sb.append("Sector: ").append(o.sector);
        if (!TextUtils.isEmpty(o.industry)) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Industry: ").append(o.industry);
        }
        return sb.toString();
    }

    private static String buildMarketCapPe(CompanyOverview o) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(o.marketCap)) sb.append("Market cap: ").append(o.marketCap);
        if (!TextUtils.isEmpty(o.peRatio)) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("P/E: ").append(o.peRatio);
        }
        return sb.toString();
    }
}
