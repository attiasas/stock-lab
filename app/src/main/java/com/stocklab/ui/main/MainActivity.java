package com.stocklab.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stocklab.R;
import com.stocklab.data.repository.ProfileRepository;
import com.stocklab.ui.analysis.AnalysisFragment;
import com.stocklab.ui.history.HistoryFragment;
import com.stocklab.ui.portfolio.PortfolioFragment;
import com.stocklab.ui.profiles.ProfilesFragment;
import com.stocklab.ui.profiles.ProfileSetupFragment;
import com.stocklab.ui.stocks.StocksFragment;

/**
 * Single Activity: shows profile list when no profile selected; otherwise main app with bottom nav.
 */
public class MainActivity extends AppCompatActivity {

    private ProfileRepository profileRepository;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;
    private long currentProfileId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileRepository = new ProfileRepository(this);
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_portfolio) {
                loadFragment(PortfolioFragment.newInstance(currentProfileId));
                return true;
            }
            if (id == R.id.nav_stocks) {
                loadFragment(StocksFragment.newInstance(currentProfileId));
                return true;
            }
            if (id == R.id.nav_history) {
                loadFragment(HistoryFragment.newInstance(currentProfileId));
                return true;
            }
            if (id == R.id.nav_analysis) {
                loadFragment(AnalysisFragment.newInstance(currentProfileId));
                return true;
            }
            return false;
        });

        currentProfileId = profileRepository.getCurrentProfileId();
        if (currentProfileId <= 0) {
            showProfileList();
        } else {
            showMainApp();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (currentProfileId <= 0) {
                    toolbar.setTitle(R.string.profiles);
                    toolbar.getMenu().clear();
                    toolbar.setNavigationIcon(null);
                } else {
                    toolbar.setTitle(R.string.app_name);
                }
            }
        });
    }

    private void showProfileList() {
        toolbar.setTitle(R.string.profiles);
        toolbar.setNavigationIcon(null);
        bottomNav.setVisibility(View.GONE);
        loadFragment(new ProfilesFragment());
    }

    private void showMainApp() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(null);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_portfolio);
        loadFragment(PortfolioFragment.newInstance(currentProfileId));
    }

    /** Called when user taps "Play" on a profile. */
    public void onProfileSelected(long profileId) {
        currentProfileId = profileId;
        profileRepository.setCurrentProfileId(profileId);
        showMainApp();
    }

    /** Called when user creates a new profile (optional: auto-login). */
    public void onProfileCreated(long profileId) {
        currentProfileId = profileId;
        profileRepository.setCurrentProfileId(profileId);
        showMainApp();
    }

    /** Navigate to create new profile flow. */
    public void navigateToNewProfile() {
        toolbar.setTitle(R.string.new_profile);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        loadFragmentWithBackStack(new ProfileSetupFragment());
    }

    /** Log out: back to profile list. */
    public void logOut() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.log_out)
            .setMessage("Switch to another profile? You can continue this simulation later.")
            .setPositiveButton(android.R.string.ok, (d, w) -> {
                profileRepository.setCurrentProfileId(-1);
                currentProfileId = -1;
                showProfileList();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
            return true;
        }
        return false;
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    /** Load fragment and add to back stack (e.g. ProfileSetup so back returns to list). */
    public void loadFragmentWithBackStack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public long getCurrentProfileId() {
        return currentProfileId;
    }
}
