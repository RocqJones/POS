package com.intoverflown.pos.ui.returngoods.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemReturnOutwardsBinding;
import com.intoverflown.pos.ui.returngoods.data.ReturnRemoteData;

import java.util.List;

public class AdapterReturnOutwards extends RecyclerView.Adapter<AdapterReturnOutwards.RVHolder> {

    List<ReturnRemoteData> rvReturnOutwards;
    ItemReturnOutwardsBinding binding;
    Context mContext;

    public AdapterReturnOutwards(List<ReturnRemoteData> rvReturnInwards, Context mContext) {
        this.rvReturnOutwards = rvReturnInwards;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterReturnOutwards.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemReturnOutwardsBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new AdapterReturnOutwards.RVHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReturnOutwards.RVHolder holder, int position) {
        holder.returnedQty.setText(rvReturnOutwards.get(position).getReturnedQuantity());
        holder.orderQty.setText(rvReturnOutwards.get(position).getOrderedQuantity());
        holder.returnedDate.setText(rvReturnOutwards.get(position).getReturnDate());
        holder.orderDate.setText(rvReturnOutwards.get(position).getOrderDate());
        holder.customerName.setText(rvReturnOutwards.get(position).getCustomer());
        holder.supplierName.setText(rvReturnOutwards.get(position).getSupplier());
        holder.returnedReason.setText(rvReturnOutwards.get(position).getReturnReason());
    }

    @Override
    public int getItemCount() {
        return rvReturnOutwards.size();
    }

    public static class RVHolder extends RecyclerView.ViewHolder {

        TextView returnedQty, orderQty, returnedDate, orderDate, customerName, supplierName, returnedReason;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            ItemReturnOutwardsBinding itemReturnOutwardsBinding = ItemReturnOutwardsBinding.bind(itemView);
            returnedQty = itemReturnOutwardsBinding.returnedQty;
            orderQty = itemReturnOutwardsBinding.orderQuantity;
            returnedDate = itemReturnOutwardsBinding.returnedDate;
            orderDate = itemReturnOutwardsBinding.orderDate;
            customerName = itemReturnOutwardsBinding.customerName;
            supplierName = itemReturnOutwardsBinding.supplierName;
            returnedReason = itemReturnOutwardsBinding.returnedReason;
        }
    }
}