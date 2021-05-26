package com.intoverflown.pos.ui.orders.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemOrdersBinding;
import com.intoverflown.pos.ui.orders.data.OrderRemoteData;

import java.util.List;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.RVHolder> {

    List<OrderRemoteData> rvOrderData;
    ItemOrdersBinding binding;
    Context mContext;

    public AdapterOrder(List<OrderRemoteData> rvOrderData, Context mContext) {
        this.rvOrderData = rvOrderData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemOrdersBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new RVHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {
        holder.orderNo.setText(rvOrderData.get(position).getOrderNo());
        holder.orderDate.setText(rvOrderData.get(position).getOrderDate());
        holder.requiredDate.setText(rvOrderData.get(position).getDateRequired());
        holder.shippingDate.setText(rvOrderData.get(position).getShippingDate());
        holder.fName.setText(rvOrderData.get(position).getFirstName());
        holder.oName.setText(rvOrderData.get(position).getOtherNames());
        holder.shippingAddress.setText(rvOrderData.get(position).getShippingAddress());
        holder.customerPhone.setText(rvOrderData.get(position).getPhone());
        holder.unitCost.setText(rvOrderData.get(position).getUnitCost());
        holder.discount.setText(rvOrderData.get(position).getDiscountAmount());
        holder.totalCost.setText(rvOrderData.get(position).getTotalOrderAmt());
    }

    @Override
    public int getItemCount() {
        return rvOrderData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView orderNo, orderDate, requiredDate, shippingDate, fName, oName, shippingAddress,
                customerPhone, unitCost, discount, totalCost;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            ItemOrdersBinding itemOrdersBinding = ItemOrdersBinding.bind(itemView);
            orderNo = itemOrdersBinding.oNumber;
            orderDate = itemOrdersBinding.orderDate;
            requiredDate = itemOrdersBinding.requiredDate;
            shippingDate = itemOrdersBinding.shippingDate;
            fName = itemOrdersBinding.fName;
            oName = itemOrdersBinding.oName;
            shippingAddress = itemOrdersBinding.address;
            customerPhone = itemOrdersBinding.phone;
            unitCost = itemOrdersBinding.unitCost;
            discount = itemOrdersBinding.discount;
            totalCost = itemOrdersBinding.totalAmount;
        }
    }
}