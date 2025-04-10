package com.jaixlabs.securepay.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaixlabs.securepay.R;
import com.jaixlabs.securepay.Repository.TransactionRepository;
import com.jaixlabs.securepay.ViewModel.TransactionViewModel;
import com.jaixlabs.securepay.ViewModel.TransactionViewModelFactory;
import com.jaixlabs.securepay.databinding.FragmentLoginBinding;
import com.jaixlabs.securepay.model.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private TransactionViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        TransactionRepository repository = new TransactionRepository(requireActivity());
        TransactionViewModelFactory factory = new TransactionViewModelFactory(repository);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);

        initSharedPrefs();
        setListeners();

        return binding.getRoot();
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

    private void setListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Username & Password required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isInternetAvailable()) {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            login(username, password);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finishAffinity(); // ✅ Close the app completely
            }
        });
    }


    private void login(String username, String password) {
        Log.d(TAG, "login: Attempting login with username: " + username);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);

        viewModel.login(username, password).observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.loginButton.setEnabled(true);

            if (result != null && result.isSuccessful() && result.body() != null) {
                String token = result.body().getToken();
                Log.d(TAG, "login: Login successful, token received: " + token);

                if (sharedPreferences != null) {
                    sharedPreferences.edit().putString("auth_token", token).apply();
                    Log.d(TAG, "login: Token saved to EncryptedSharedPreferences");
                } else {
                    Log.e(TAG, "login: SharedPreferences is null");
                }

                Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_homeFragment);
                Log.d(TAG, "login: Navigating to HomeFragment");
            } else {
                String message = "Login Failed";
                Log.e(TAG, "login: Login failed");

                if (result == null) {
                    message = "No response from server";
                    Log.e(TAG, "login: No response from server");
                } else if (result.errorBody() != null) {
                    try {
                        message = result.errorBody().string();
                        Log.e(TAG, "login: Error message from server: " + message);
                    } catch (IOException e) {
                        Log.e(TAG, "login: Error parsing errorBody", e);
                    }
                }

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        Log.d(TAG, "isInternetAvailable: " + isConnected);
        return isConnected;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
