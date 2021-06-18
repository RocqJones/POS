package com.intoverflown.pos.ui.merchantbranch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemBranchesBinding;
import com.intoverflown.pos.ui.merchantbranch.data.BranchRemoteData;

import java.util.List;

public class AdapterBranchMerchant extends RecyclerView.Adapter<AdapterBranchMerchant.RVHolder> {

    List<BranchRemoteData> rvBranchData;
    ItemBranchesBinding binding;
    Context mContext;

    public AdapterBranchMerchant(List<BranchRemoteData> rvBranchData, Context mContext) {
        this.rvBranchData = rvBranchData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterBranchMerchant.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemBranchesBinding.inflate(LayoutInflater.from(parent.getContext()));
        View view = binding.getRoot();
        return new AdapterBranchMerchant.RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBranchMerchant.RVHolder holder, int position) {
        holder.branchName.setText(rvBranchData.get(position).getBranchName());
        holder.address.setText(rvBranchData.get(position).getAddress());
        holder.phone.setText(rvBranchData.get(position).getPhone());
        holder.contactPerson.setText(rvBranchData.get(position).getContactPerson());
        holder.merchantName.setText(rvBranchData.get(position).getMerchants());
        holder.region.setText(rvBranchData.get(position).getRegionId());
    }

    @Override
    public int getItemCount() {
        return rvBranchData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView branchName, address, phone, contactPerson, merchantName, region;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            ItemBranchesBinding itemBranchesBinding = ItemBranchesBinding.bind(itemView);
            branchName = itemBranchesBinding.branchName;
            address = itemBranchesBinding.address;
            phone = itemBranchesBinding.phone;
            contactPerson = itemBranchesBinding.contactPerson;
            merchantName = itemBranchesBinding.merchant;
            region = itemBranchesBinding.region;
        }
    }
}