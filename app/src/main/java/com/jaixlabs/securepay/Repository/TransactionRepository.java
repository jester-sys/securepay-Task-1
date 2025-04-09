package com.jaixlabs.securepay.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaixlabs.securepay.Api.ApiClient;
import com.jaixlabs.securepay.Api.ApiService;

import com.jaixlabs.securepay.db.AppDatabase;
import com.jaixlabs.securepay.db.TransactionDao;
import com.jaixlabs.securepay.model.LoginRequest;
import com.jaixlabs.securepay.model.LoginResponse;
import com.jaixlabs.securepay.model.Transaction;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.*;

public class TransactionRepository {
    private final ApiService apiService;
    private final TransactionDao transactionDao;
    private final SharedPreferences sharedPreferences;

    public TransactionRepository(Context context) {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
        AppDatabase db = AppDatabase.getInstance(context);
        this.transactionDao = db.transactionDao();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LiveData<Response<LoginResponse>> login(String username, String password) {
        MutableLiveData<Response<LoginResponse>> result = new MutableLiveData<>();

        apiService.login(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                result.postValue(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                result.postValue(null);
            }
        });

        return result;
    }

    public LiveData<List<Transaction>> fetchTransactions(String token) {
        MutableLiveData<List<Transaction>> result = new MutableLiveData<>();

        apiService.getTransactions("Bearer " + token).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> apiData = response.body();
                    result.postValue(apiData);

                    // ✅ Check if already synced using last_sync_id or hash
                    int lastSavedId = sharedPreferences.getInt("last_transaction_id", -1);
                    int newLastId = getMaxId(apiData);

                    if (newLastId != lastSavedId) {
                        // ✅ Insert into Room DB
                        Executors.newSingleThreadExecutor().execute(() -> {
                            transactionDao.insertAll(apiData);
                            sharedPreferences.edit().putInt("last_transaction_id", newLastId).apply();
                        });
                    }
                } else {
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                result.postValue(null);
            }
        });

        return result;
    }

    private int getMaxId(List<Transaction> transactions) {
        int max = -1;
        for (Transaction t : transactions) {
            try {
                int id = Integer.parseInt(t.getId());
                if (id > max) {
                    max = id;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace(); // optional: log error
            }
        }
        return max;
    }

    public LiveData<List<Transaction>> getTransactionsFromDb() {
        return transactionDao.getAllTransactions();
    }
}
