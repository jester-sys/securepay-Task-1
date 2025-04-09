package com.jaixlabs.securepay.Adapter;

import android.annotation.SuppressLint;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jaixlabs.securepay.R;
import com.jaixlabs.securepay.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.ViewHolder> {

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Transaction>() {
                @Override
                public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                    return oldItem.getId().equals(newItem.getId()); // or use unique ID field
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAmount, txtDate,cat,desc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDate = itemView.findViewById(R.id.txtDate);
            cat = itemView.findViewById(R.id.txtCategory);
            desc =itemView.findViewById(R.id.txtDescription);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = getItem(position);
        holder.txtAmount.setText("₹" + String.format("%.2f", t.getAmount()));
        holder.txtDate.setText(t.getDate());
        holder.cat.setText(t.getCategory());
        holder.desc.setText(t.getDescription());
    }
    public void setTransactions(List<Transaction> newList) {
        submitList(new ArrayList<>(newList)); // defensive copy
    }

}
