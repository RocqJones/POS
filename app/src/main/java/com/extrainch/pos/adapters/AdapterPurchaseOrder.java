package com.extrainch.pos.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterPurchaseOrder extends RecyclerView.Adapter<AdapterPurchaseOrder.RvHolder> {

    @NotNull
    @Override
    public AdapterPurchaseOrder.RvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPurchaseOrder.RvHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RvHolder extends RecyclerView.ViewHolder {
        public RvHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
