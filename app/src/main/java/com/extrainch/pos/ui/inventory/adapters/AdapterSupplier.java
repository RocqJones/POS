package com.extrainch.pos.ui.inventory.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ItemSupplierBinding;
import com.extrainch.pos.ui.inventory.data.InventoryRemoteData;
import com.extrainch.pos.ui.inventory.fragments.SupplierFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterSupplier extends RecyclerView.Adapter<AdapterSupplier.RvHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<InventoryRemoteData> supplierData;
    ItemSupplierBinding binding;
    Context mContext;

    SupplierFragment supplierFragment = new SupplierFragment();

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
        holder.editBtn.setOnClickListener(v -> {
            String sId = supplierData.get(position).getItemCatId();
            String mId = supplierData.get(position).getMerchantId();
            String txt1 = supplierData.get(position).getName();
            String txt2 = supplierData.get(position).getAddress();
            String txt3 = supplierData.get(position).getPhone();
            String txt4 = supplierData.get(position).getEmail();
            String txt5 = supplierData.get(position).getRemarks();
            String createdBy = supplierData.get(position).getCreatedById();

            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenS = preferences.getString(KEY_TOKEN, "Token");

            // start dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_supplier);
            dialog.setCancelable(false);

            EditText name = (EditText) dialog.findViewById(R.id.name);
            EditText address = (EditText) dialog.findViewById(R.id.address);
            EditText phone = (EditText) dialog.findViewById(R.id.phone);
            EditText email = (EditText) dialog.findViewById(R.id.email);
            EditText remarks = (EditText) dialog.findViewById(R.id.remarks);

            Button svBtn = (Button) dialog.findViewById(R.id.saveBtn);

            name.setText(txt1);
            address.setText(txt2);
            phone.setText(txt3);
            remarks.setText(txt4);
            email.setText(txt5);

            svBtn.setOnClickListener(v1 -> {
                dialog.dismiss();
                String gNm = name.getText().toString().trim();
                String gAd = address.getText().toString().trim();
                String gPh = phone.getText().toString().trim();
                String gEm = email.getText().toString().trim();
                String gRm = remarks.getText().toString().trim();

                supplierFragment.modifyData(sId, mId, gNm, gAd, gPh, gEm, gRm, createdBy, tokenS);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        });
    }

    @Override
    public int getItemCount() {
        return supplierData.size();
    }

    static class RvHolder extends RecyclerView.ViewHolder {

        TextView sName, address, phone, email, mName, remarks;
        FloatingActionButton editBtn;

        public RvHolder(@NonNull View itemView) {
            super(itemView);
            ItemSupplierBinding itemSupplierBinding = ItemSupplierBinding.bind(itemView);
            sName = itemSupplierBinding.sName;
            address = itemSupplierBinding.address;
            phone = itemSupplierBinding.phone;
            email = itemSupplierBinding.email;
            mName = itemSupplierBinding.merchantName;
            remarks = itemSupplierBinding.sRemarks;
            editBtn = itemSupplierBinding.edit;
        }
    }
}
