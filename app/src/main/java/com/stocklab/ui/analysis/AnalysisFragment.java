package com.stocklab.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.stocklab.R;

public class AnalysisFragment extends Fragment {

    private static final String ARG_PROFILE_ID = "profile_id";

    private long profileId;
    private AnalysisViewModel viewModel;

    public static AnalysisFragment newInstance(long profileId) {
        AnalysisFragment f = new AnalysisFragment();
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
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);
        viewModel.init(requireContext(), profileId);

        TextView scoreValue = view.findViewById(R.id.score_value);
        TextView returnPercent = view.findViewById(R.id.return_percent);
        TextView transactionsCount = view.findViewById(R.id.transactions_count);

        viewModel.getScore().observe(getViewLifecycleOwner(), s -> scoreValue.setText(s != null ? s : "—"));
        viewModel.getReturnPercentText().observe(getViewLifecycleOwner(), returnPercent::setText);
        viewModel.getTransactionsCountText().observe(getViewLifecycleOwner(), transactionsCount::setText);
    }
}
