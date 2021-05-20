package com.intoverflown.pos.ui.customers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemCustomerBinding;
import com.intoverflown.pos.ui.customers.data.Customer;

import java.util.List;

public class AdapterCustomer extends RecyclerView.Adapter<AdapterCustomer.RVHolder>{
    List<Customer> rvCustomerData;
    ItemCustomerBinding binding;
    Context mContext;

    public AdapterCustomer(List<Customer> rvCustomerData, Context mContext) {
        this.rvCustomerData = rvCustomerData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.getContext()));
        View view = binding.getRoot();
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {
        holder.fName.setText(rvCustomerData.get(position).getFirstName());
        holder.oName.setText(rvCustomerData.get(position).getOtherNames());
        holder.address.setText(rvCustomerData.get(position).getAddress());
        holder.phone.setText(rvCustomerData.get(position).getPhone());
        holder.email.setText(rvCustomerData.get(position).getEmail());
        holder.mName.setText(rvCustomerData.get(position).getMerchantName());
    }

    @Override
    public int getItemCount() {
        return rvCustomerData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView fName, oName, address, phone, email, mName;

        public RVHolder(View view) {
            super(view);
            ItemCustomerBinding itemCustomerBinding = ItemCustomerBinding.bind(view);
            fName = itemCustomerBinding.fName;
            oName = itemCustomerBinding.otherName;
            phone = itemCustomerBinding.phone;
            email = itemCustomerBinding.email;
            mName = itemCustomerBinding.merchantName;
            address = itemCustomerBinding.address;
        }
    }
}
