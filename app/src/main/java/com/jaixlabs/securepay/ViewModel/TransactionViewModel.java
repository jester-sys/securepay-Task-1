package com.jaixlabs.securepay.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jaixlabs.securepay.Repository.TransactionRepository;
import com.jaixlabs.securepay.model.LoginResponse;
import com.jaixlabs.securepay.model.Transaction;

import java.util.List;

import retrofit2.Response;

public class TransactionViewModel extends ViewModel {
    private TransactionRepository repository;
    private MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();

    public TransactionViewModel(TransactionRepository repo) {
        this.repository = repo;
    }

    public LiveData<Response<LoginResponse>> login(String username, String password) {
        return repository.login(username, password);
    }

    public void loadTransactions(String token) {
        repository.fetchTransactions(token).observeForever(transactions::setValue);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }
    public LiveData<List<Transaction>> getLocalTransactions() {
        return repository.getTransactionsFromDb();
    }

}
