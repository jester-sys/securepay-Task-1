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
        // Show ProgressBar, Hide Button
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);

        viewModel.login(username, password).observe(getViewLifecycleOwner(), result -> {
            // Hide ProgressBar, Enable Button
            binding.progressBar.setVisibility(View.GONE);
            binding.loginButton.setEnabled(true);

            if (result != null && result.isSuccessful() && result.body() != null) {
                String token = result.body().getToken();
                sharedPreferences.edit().putString("auth_token", token).apply();

                Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_homeFragment);
            } else {
                String message = "Login Failed";

                if (result == null) {
                    message = "No response from server";
                } else if (result.errorBody() != null) {
                    try {
                        message = result.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
