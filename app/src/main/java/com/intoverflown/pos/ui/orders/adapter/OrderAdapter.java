package com.intoverflown.pos.ui.orders.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.RVHolder> {
    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RVHolder extends RecyclerView.ViewHolder {
        public RVHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
