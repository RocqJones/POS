package com.extrainch.pos.ui.returngoods.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.pos.databinding.ItemReturnInwardsBinding;
import com.extrainch.pos.ui.returngoods.data.ReturnRemoteData;

import java.util.List;

public class AdapterReturnInwards extends RecyclerView.Adapter<AdapterReturnInwards.RVHolder> {

    List<ReturnRemoteData> rvReturnInwards;
    ItemReturnInwardsBinding binding;
    Context mContext;

    public AdapterReturnInwards(List<ReturnRemoteData> rvReturnInwards, Context mContext) {
        this.rvReturnInwards = rvReturnInwards;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterReturnInwards.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemReturnInwardsBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new RVHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReturnInwards.RVHolder holder, int position) {
        holder.returnedQty.setText(rvReturnInwards.get(position).getReturnedQuantity());
        holder.orderQty.setText(rvReturnInwards.get(position).getOrderedQuantity());
        holder.returnedDate.setText(rvReturnInwards.get(position).getReturnDate());
        holder.orderDate.setText(rvReturnInwards.get(position).getOrderDate());
        holder.customerName.setText(rvReturnInwards.get(position).getCustomer());
        holder.supplierName.setText(rvReturnInwards.get(position).getSupplier());
        holder.returnedReason.setText(rvReturnInwards.get(position).getReturnReason());
    }

    @Override
    public int getItemCount() {
        return rvReturnInwards.size();
    }

    public static class RVHolder extends RecyclerView.ViewHolder {

        TextView returnedQty, orderQty, returnedDate, orderDate, customerName, supplierName, returnedReason;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            ItemReturnInwardsBinding itemReturnInwardsBinding = ItemReturnInwardsBinding.bind(itemView);
            returnedQty = itemReturnInwardsBinding.returnedQty;
            orderQty = itemReturnInwardsBinding.orderQuantity;
            returnedDate = itemReturnInwardsBinding.returnedDate;
            orderDate = itemReturnInwardsBinding.orderDate;
            customerName = itemReturnInwardsBinding.customerName;
            supplierName = itemReturnInwardsBinding.supplierName;
            returnedReason = itemReturnInwardsBinding.returnedReason;
        }
    }
}