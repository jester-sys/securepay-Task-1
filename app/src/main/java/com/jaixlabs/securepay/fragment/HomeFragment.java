package com.jaixlabs.securepay.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.jaixlabs.securepay.Adapter.TransactionAdapter;
import com.jaixlabs.securepay.R;
import com.jaixlabs.securepay.Repository.TransactionRepository;
import com.jaixlabs.securepay.Util.BiometricUtil;
import com.jaixlabs.securepay.Util.NetworkUtils;
import com.jaixlabs.securepay.Util.PrefUtil;
import com.jaixlabs.securepay.ViewModel.TransactionViewModel;
import com.jaixlabs.securepay.ViewModel.TransactionViewModelFactory;
import com.jaixlabs.securepay.databinding.FragmentHomeBinding;
import com.jaixlabs.securepay.databinding.FragmentLoginBinding;
import com.jaixlabs.securepay.model.Transaction;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TransactionViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initSharedPrefs();

        TransactionRepository repository = new TransactionRepository(requireActivity());
        TransactionViewModelFactory factory = new TransactionViewModelFactory(repository);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);

        setupRecyclerView();
        setupSearchListener();
        binding.settingsBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_settingsFragment);
        });



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (sharedPreferences != null) {
                String token = sharedPreferences.getString("auth_token", "");
                Log.d("HomeFragment", "Token: " + token);

                if (token == null || token.isEmpty()) {
                    Log.d("HomeFragment", "Token is empty or null. Navigating to Login.");
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_homeFragment_to_loginFragment);
                } else {
                    Log.d("HomeFragment", "Token is valid. Proceeding to biometric prompt.");
                    showBiometricPrompt(token);
                }
            } else {
                Log.e("HomeFragment", "sharedPreferences is null!");
            }
        }, 300); // Delay added
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

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(); // using ListAdapter
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearchListener() {
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                String selectedCategory = binding.categorySpinner.getSelectedItem().toString();
                filterBySearchAndCategory(query, selectedCategory);

                List<Transaction> filtered = new ArrayList<>();
                for (Transaction t : allTransactions) {
                    if (
                            String.valueOf(t.getAmount()).toLowerCase().contains(query) ||
                                    t.getDate().toLowerCase().contains(query) ||
                                    t.getCategory().toLowerCase().contains(query) ||
                                    t.getDescription().toLowerCase().contains(query)
                    ) {
                        filtered.add(t);
                    }
                }
                adapter.submitList(filtered);
            }
        });
    }


    private void showBiometricPrompt(String token) {
        BiometricUtil.showBiometricPrompt(requireActivity(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                //   fetchTransactions(token);
                fetchDataBasedOnNetwork(token);
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_CANCELED) {
                    Toast.makeText(getContext(), "Biometric Error: " + errString, Toast.LENGTH_SHORT).show();
                }
                showBiometricFallbackDialog(token);
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(getContext(), "Biometric authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataBasedOnNetwork(String token) {
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            fetchTransactions(token);
        } else {
            fetchFromRoom(); // Internet off
        }
    }

    private void fetchFromRoom() {
        viewModel.getLocalTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                allTransactions.clear();
                allTransactions.addAll(transactions);
                adapter.setTransactions(allTransactions);
                setupCategorySpinner();
            }
        });
    }

    private void fetchTransactions(String token) {
        viewModel.loadTransactions(token);
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                adapter.submitList(transactions);
                setupCategorySpinner();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showBiometricFallbackDialog(String token) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Biometric Authentication")
                .setMessage("Biometric authentication was not completed. Do you want to continue without it?")
                .setPositiveButton("Continue", (dialog, which) -> {
                    //    fetchTransactions(token);
                    fetchDataBasedOnNetwork(token);
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    showBiometricPrompt(token);
                })
                .setCancelable(false)
                .show();
    }
    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        for (Transaction t : allTransactions) {
            if (!categories.contains(t.getCategory())) {
                categories.add(t.getCategory());
            }
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(adapterSpinner);

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                filterBySearchAndCategory(binding.search.getText().toString(), selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    private void filterBySearchAndCategory(String query, String category) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            boolean matchCategory = category.equals("All") || t.getCategory().equalsIgnoreCase(category);
            boolean matchSearch = String.valueOf(t.getAmount()).toLowerCase().contains(query)
                    || t.getDate().toLowerCase().contains(query)
                    || t.getDescription().toLowerCase().contains(query);

            if (matchCategory && matchSearch) {
                filtered.add(t);
            }
        }
        adapter.submitList(filtered);
    }



}

