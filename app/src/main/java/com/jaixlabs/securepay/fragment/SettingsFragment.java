package com.jaixlabs.securepay.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaixlabs.securepay.R;


public class SettingsFragment extends Fragment {

    private Spinner themeSpinner;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;
    private ImageView backBtn;

    private final String[] themeOptions = {"System Default", "Light", "Dark"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        themeSpinner = view.findViewById(R.id.themeSpinner);
        logoutButton = view.findViewById(R.id.logoutButton);
        backBtn = view.findViewById(R.id.backBtn);

        initSharedPrefs();
        setupThemeSpinner();
        setupLogoutButton();

      backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(SettingsFragment.this)
                    .navigate(R.id.action_settingsFragment_to_homeFragment);
        });

        return view;
    }

    private void initSharedPrefs() {
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    requireContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupThemeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, themeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);

        // Load saved theme mode
        int savedMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (savedMode) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                themeSpinner.setSelection(2);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                themeSpinner.setSelection(1);
                break;
            default:
                themeSpinner.setSelection(0);
                break;
        }

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedMode;
                switch (position) {
                    case 1:
                        selectedMode = AppCompatDelegate.MODE_NIGHT_NO;
                        break;
                    case 2:
                        selectedMode = AppCompatDelegate.MODE_NIGHT_YES;
                        break;
                    default:
                        selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                        break;
                }

                // Save selected mode
                sharedPreferences.edit().putInt("theme_mode", selectedMode).apply();

                // Apply theme
                AppCompatDelegate.setDefaultNightMode(selectedMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setIcon(R.drawable.ic_logout) // optional: your own icon
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear all encrypted preferences
                        sharedPreferences.edit().clear().apply();

                        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                        NavHostFragment.findNavController(SettingsFragment.this)
                                .navigate(R.id.action_settingsFragment_to_loginFragment);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_settingsFragment_to_homeFragment);
            }
        });
    }

}
