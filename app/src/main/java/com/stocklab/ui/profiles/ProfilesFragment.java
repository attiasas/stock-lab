package com.stocklab.ui.profiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stocklab.R;
import com.stocklab.ui.main.MainActivity;
import com.stocklab.model.Profile;
import com.stocklab.util.AnalysisHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfilesFragment extends Fragment {

    private ProfilesViewModel viewModel;
    private ProfileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        viewModel.init(requireContext());

        RecyclerView recycler = view.findViewById(R.id.recycler_profiles);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProfileAdapter(new ArrayList<>(), this::onPlay, this::onDelete);
        recycler.setAdapter(adapter);

        viewModel.getProfiles().observe(getViewLifecycleOwner(), profiles -> {
            if (profiles != null) adapter.setItems(profiles);
        });

        view.findViewById(R.id.btn_new_profile).setOnClickListener(v -> {
            MainActivity activity = (MainActivity) requireActivity();
            activity.navigateToNewProfile();
        });
    }

    private void onPlay(Profile profile) {
        ((MainActivity) requireActivity()).onProfileSelected(profile.id);
    }

    private void onDelete(Profile profile) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete profile?")
            .setMessage("This will delete \"" + profile.name + "\" and all its data.")
            .setPositiveButton(android.R.string.ok, (d, w) -> {
                viewModel.deleteProfile(profile.id);
                Toast.makeText(requireContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    public static class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.VH> {
        private List<Profile> items = new ArrayList<>();
        private final OnPlayListener onPlay;
        private final OnDeleteListener onDelete;

        interface OnPlayListener { void onPlay(Profile p); }
        interface OnDeleteListener { void onDelete(Profile p); }

        ProfileAdapter(List<Profile> items, OnPlayListener onPlay, OnDeleteListener onDelete) {
            this.items = items;
            this.onPlay = onPlay;
            this.onDelete = onDelete;
        }

        void setItems(List<Profile> items) {
            this.items = items != null ? items : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Profile p = items.get(position);
            holder.name.setText(p.name);
            double cash = p.startingCash / 100.0;
            holder.detail.setText(String.format(Locale.US, "%s · %s · %s%.2f",
                p.currency, AnalysisHelper.difficultyLabel(p.difficulty), p.currency.equals("USD") ? "$" : p.currency + " ", cash));
            holder.btnPlay.setOnClickListener(v -> onPlay.onPlay(p));
            holder.btnDelete.setOnClickListener(v -> onDelete.onDelete(p));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            final android.widget.TextView name, detail;
            final com.google.android.material.button.MaterialButton btnPlay, btnDelete;

            VH(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.profile_name);
                detail = itemView.findViewById(R.id.profile_detail);
                btnPlay = itemView.findViewById(R.id.btn_play);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}
