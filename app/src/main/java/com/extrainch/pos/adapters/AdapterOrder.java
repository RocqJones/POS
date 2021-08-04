package com.extrainch.pos.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ItemOrdersBinding;
import com.extrainch.pos.repository.OrderRemoteData;
import com.extrainch.pos.ui.orders.OrdersActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.RVHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<OrderRemoteData> rvOrderData;
    ItemOrdersBinding binding;
    Context mContext;

    OrdersActivity ordersActivity = new OrdersActivity();

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
        holder.orderEdit.setOnClickListener(v -> {
            String mId = rvOrderData.get(position).getMerchantId();
            String cId = rvOrderData.get(position).getCustomerId();
            String oId = rvOrderData.get(position).getOrderId();
            String txt1 = rvOrderData.get(position).getOrderNo();
            String txt2 = rvOrderData.get(position).getOrderDate();
            String txt3 = rvOrderData.get(position).getDateRequired();
            String txt4 = rvOrderData.get(position).getTotalOrderAmt();
            String txt5 = rvOrderData.get(position).getShippingDate();
            String txt6 = rvOrderData.get(position).getShippingAddress();
            String txt7 = rvOrderData.get(position).getUnitCost();
            String txt8 = rvOrderData.get(position).getDiscountAmount();
            String createdBy = rvOrderData.get(position).getCreatedBy();

            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenO = preferences.getString(KEY_TOKEN, "Token");

            // start dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_order);
            dialog.setCancelable(false);

            EditText orderNo = (EditText) dialog.findViewById(R.id.orderId);
            EditText orderDate = (EditText) dialog.findViewById(R.id.orderDate);
            EditText requiredDate = (EditText) dialog.findViewById(R.id.orderDateRequired);
            EditText unitCost = (EditText) dialog.findViewById(R.id.orderUnitCost);
            EditText quantity = (EditText) dialog.findViewById(R.id.orderQuantity);
            EditText discount = (EditText) dialog.findViewById(R.id.discount);
            EditText totalDue = (EditText) dialog.findViewById(R.id.totalDue);
            Spinner paymentType = (Spinner) dialog.findViewById(R.id.paymentType);
            EditText shippingDate = (EditText) dialog.findViewById(R.id.shippingDate);
            EditText shippingAddress = (EditText) dialog.findViewById(R.id.shippingAddress);
            Spinner status = (Spinner) dialog.findViewById(R.id.status);

            AppCompatButton svBtn = (AppCompatButton) dialog.findViewById(R.id.saveBtn);

            orderNo.setText(txt1);
            orderDate.setText(txt2);
            requiredDate.setText(txt3);
            totalDue.setText(txt4);
            unitCost.setText(txt7);
            discount.setText(txt8);

            String[] payT = {"Select...", "Cash", "Mpesa", "Card"};
            ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_dropdown_item_1line, payT);
            paymentType.setAdapter(adapterUnit);

            String[] pStatus = {"Select...", "Quote", "Delivered", "Shipping", "In Stock", "Out of stock"};
            ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_dropdown_item_1line, pStatus);
            status.setAdapter(adapterStatus);

            svBtn.setOnClickListener(v1 -> {
                String oNo = orderNo.getText().toString().trim();
                String oDt = orderDate.getText().toString().trim();
                String oRt = requiredDate.getText().toString().trim();
                String oUc = unitCost.getText().toString().trim();
                String oDc = discount.getText().toString().trim();
                String oTd = totalDue.getText().toString().trim();
                String oPt = paymentType.getSelectedItem().toString().trim();
                String oSd = shippingDate.getText().toString().trim();
                String oSa = shippingAddress.getText().toString().trim();
                String oSt = status.getSelectedItem().toString().trim();
                String oQt = quantity.getText().toString().trim();

                Log.d("data", mId + cId + oId + " " + createdBy + " " + tokenO);
                ordersActivity.modifyProduct(mId, cId, oId, oQt, oNo, oDt, oRt, oUc, oDc, oTd, oPt,
                        oSd, oSa, oSt, createdBy, tokenO);
                dialog.dismiss();
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        });
    }

    @Override
    public int getItemCount() {
        return rvOrderData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView orderNo, orderDate, requiredDate, shippingDate, fName, oName, shippingAddress,
                customerPhone, unitCost, discount, totalCost;
        FloatingActionButton orderEdit;

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
            orderEdit = itemOrdersBinding.edit;
        }
    }
}