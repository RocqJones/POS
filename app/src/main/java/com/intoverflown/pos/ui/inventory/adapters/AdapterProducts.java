package com.intoverflown.pos.ui.inventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemProductsBinding;
import com.intoverflown.pos.ui.inventory.data.InventoryRemoteData;

import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.RVHolder> {

    List<InventoryRemoteData> productData;
    ItemProductsBinding binding;
    Context mContext;

    public AdapterProducts(List<InventoryRemoteData> productData, Context mContext) {
        this.productData = productData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemProductsBinding.inflate(LayoutInflater.from(parent.getContext()));
        View view = binding.getRoot();
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {
        holder.pName.setText(productData.get(position).getName());
        holder.pUnits.setText(productData.get(position).getUnits());
        holder.pStatus.setText(productData.get(position).getStatusId());
        holder.pReOderLevel.setText(productData.get(position).getReOrderLevel());
        holder.pCategory.setText(productData.get(position).getCategory());
        holder.pSupplier.setText(productData.get(position).getSupplier());
        holder.pRemarks.setText(productData.get(position).getRemarks());
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView pName, pUnits, pStatus, pReOderLevel, pCategory, pSupplier, pRemarks;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            ItemProductsBinding itemProductsBinding = ItemProductsBinding.bind(itemView);
            pName = itemProductsBinding.pName;
            pUnits = itemProductsBinding.pUnits;
            pStatus = itemProductsBinding.pStatus;
            pReOderLevel = itemProductsBinding.pReorderLevel;
            pCategory = itemProductsBinding.pCategoryName;
            pSupplier = itemProductsBinding.pSupplier;
            pRemarks = itemProductsBinding.pRemarks;
        }
    }
}
