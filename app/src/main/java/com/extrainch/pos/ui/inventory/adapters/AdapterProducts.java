package com.extrainch.pos.ui.inventory.adapters;

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
import com.extrainch.pos.databinding.ItemProductsBinding;
import com.extrainch.pos.ui.inventory.data.InventoryRemoteData;
import com.extrainch.pos.ui.inventory.fragments.ProductFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.RVHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<InventoryRemoteData> productData;
    ItemProductsBinding binding;
    Context mContext;

    ProductFragment productFragment = new ProductFragment();

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
        holder.pEditBtn.setOnClickListener(v -> {
            Log.d("editClicked", "Clicked");
            String pId = productData.get(position).getpId();
            String cId = productData.get(position).getpCatId();
            String sId = productData.get(position).getpSupId();
            String mId = productData.get(position).getpMerchId();
            String txt1 = productData.get(position).getName();
            String txt2 = productData.get(position).getReOrderLevel();
            String txt3 = productData.get(position).getpQuantity();
            String txt4 = productData.get(position).getpUnitMeasure();
            String txt5 = productData.get(position).getRemarks();
            String createdBy = productData.get(position).getCreatedById();

            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenS = preferences.getString(KEY_TOKEN, "Token");

            // start dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_product);
            dialog.setCancelable(false);

            EditText name = (EditText) dialog.findViewById(R.id.name);
            EditText quantity = (EditText) dialog.findViewById(R.id.quantity);
            EditText reLevel = (EditText) dialog.findViewById(R.id.reorderLevel);
            Spinner unitMeasure = (Spinner) dialog.findViewById(R.id.uitsOfMeasure);
            EditText remarks = (EditText) dialog.findViewById(R.id.remarks);
            Spinner status = (Spinner) dialog.findViewById(R.id.status);

            AppCompatButton svBtn = (AppCompatButton) dialog.findViewById(R.id.saveBtn);

            name.setText(txt1);
            reLevel.setText(txt2);
            quantity.setText(txt3);

            String[] unitsOfMeasure = {"Select...", "kgs", "gms", "Lt" , "ml"};
            ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_dropdown_item_1line, unitsOfMeasure);
            unitMeasure.setAdapter(adapterUnit);

            remarks.setText(txt5);

            String[] pStatus = {"Select...", "Delivered", "In Stock", "Out of stock" , "other"};
            ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_dropdown_item_1line, pStatus);
            status.setAdapter(adapterStatus);

            svBtn.setOnClickListener(v1 -> {
                dialog.dismiss();
                String pNm = name.getText().toString().trim();
                String pQt = quantity.getText().toString().trim();
                String pRl = reLevel.getText().toString().trim();
                String pUm = unitMeasure.getSelectedItem().toString().trim();
                String pRm = remarks.getText().toString().trim();
                String pSt = status.getSelectedItem().toString().trim();

                Log.d("data", pId + cId + sId + mId + " " + createdBy + " " + tokenS);
                productFragment.modifyProduct(pId, cId, sId, mId, pNm, pQt, pRl, pUm, pRm, pSt, createdBy, tokenS);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        });
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    static class RVHolder extends RecyclerView.ViewHolder {

        TextView pName, pUnits, pStatus, pReOderLevel, pCategory, pSupplier, pRemarks;
        FloatingActionButton pEditBtn;

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
            pEditBtn = itemProductsBinding.prodEdit;
        }
    }
}
