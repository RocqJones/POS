package com.extrainch.pos.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterInventory extends RecyclerView.Adapter<AdapterInventory.RHolder> {
    @NonNull
    @NotNull
    @Override
    public RHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class RHolder extends RecyclerView.ViewHolder {
        public RHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
