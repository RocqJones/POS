package com.intoverflown.pos.ui.inventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemSupplierBinding;
import com.intoverflown.pos.ui.inventory.data.InventoryRemoteData;

import java.util.List;

public class AdapterSupplier extends RecyclerView.Adapter<AdapterSupplier.RvHolder> {

    List<InventoryRemoteData> supplierData;
    ItemSupplierBinding binding;
    Context mContext;

    public AdapterSupplier(List<InventoryRemoteData> supplierData, Context mContext) {
        this.supplierData = supplierData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSupplierBinding.inflate(LayoutInflater.from(parent.getContext()));
        View view = binding.getRoot();
        return new RvHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvHolder holder, int position) {
        holder.sName.setText(supplierData.get(position).getName());
        holder.address.setText(supplierData.get(position).getAddress());
        holder.phone.setText(supplierData.get(position).getPhone());
        holder.email.setText(supplierData.get(position).getEmail());
        holder.mName.setText(supplierData.get(position).getMerchantName());
        holder.remarks.setText(supplierData.get(position).getRemarks());
    }

    @Override
    public int getItemCount() {
        return supplierData.size();
    }

    static class RvHolder extends RecyclerView.ViewHolder {

        TextView sName, address, phone, email, mName, remarks;

        public RvHolder(@NonNull View itemView) {
            super(itemView);
            ItemSupplierBinding itemSupplierBinding = ItemSupplierBinding.bind(itemView);
            sName = itemSupplierBinding.sName;
            address = itemSupplierBinding.address;
            phone = itemSupplierBinding.phone;
            email = itemSupplierBinding.email;
            mName = itemSupplierBinding.merchantName;
            remarks = itemSupplierBinding.sRemarks;
        }
    }
}
