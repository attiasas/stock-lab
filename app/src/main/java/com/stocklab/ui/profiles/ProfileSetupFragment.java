package com.stocklab.ui.profiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.stocklab.R;
import com.stocklab.ui.main.MainActivity;

public class ProfileSetupFragment extends Fragment {

    private TextInputEditText inputName, inputCapital, inputCurrency;
    private RadioGroup radioDifficulty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputName = view.findViewById(R.id.input_name);
        inputCapital = view.findViewById(R.id.input_capital);
        inputCurrency = view.findViewById(R.id.input_currency);
        radioDifficulty = view.findViewById(R.id.radio_difficulty);

        view.findViewById(R.id.btn_create).setOnClickListener(v -> createProfile());
    }

    private void createProfile() {
        String name = inputName.getText() != null ? inputName.getText().toString().trim() : "";
        String capitalStr = inputCapital.getText() != null ? inputCapital.getText().toString().trim() : "";
        String currency = inputCurrency.getText() != null ? inputCurrency.getText().toString().trim().toUpperCase() : "USD";
        if (currency.isEmpty()) currency = "USD";

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a profile name", Toast.LENGTH_SHORT).show();
            return;
        }
        double capitalD;
        try {
            capitalD = Double.parseDouble(capitalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Enter a valid starting capital", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capitalD <= 0) {
            Toast.makeText(requireContext(), "Starting capital must be positive", Toast.LENGTH_SHORT).show();
            return;
        }
        long startingCashCents = Math.round(capitalD * 100);

        int difficulty = 1; // normal
        int checkedId = radioDifficulty.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_easy) difficulty = 0;
        else if (checkedId == R.id.radio_hard) difficulty = 2;

        ProfileSetupViewModel vm = new ViewModelProvider(requireActivity()).get(ProfileSetupViewModel.class);
        vm.init(requireContext());
        long id = vm.createProfile(name, startingCashCents, currency, difficulty);
        if (id > 0) {
            Toast.makeText(requireContext(), "Profile created", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).onProfileCreated(id);
        } else {
            Toast.makeText(requireContext(), "Failed to create profile", Toast.LENGTH_SHORT).show();
        }
    }
}
