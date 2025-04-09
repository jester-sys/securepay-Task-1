package com.jaixlabs.securepay.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaixlabs.securepay.R;

public class SplashFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false); // layout simple splash UI
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSharedPrefs();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String token = sharedPreferences.getString("auth_token", null);

            if (token != null && !token.isEmpty()) {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_splashFragment_to_homeFragment);
            } else {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_splashFragment_to_loginFragment);
            }
        }, 1500); // delay just for splash effect
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
}
