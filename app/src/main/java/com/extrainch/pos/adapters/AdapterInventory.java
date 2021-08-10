package com.extrainch.pos.adapters;

import android.app.DatePickerDialog;
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
import com.extrainch.pos.databinding.ItemInventoryBinding;
import com.extrainch.pos.databinding.ItemOrdersBinding;
import com.extrainch.pos.repository.DataInventoryMgt;
import com.extrainch.pos.repository.OrderRemoteData;
import com.extrainch.pos.ui.inventory.InventoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterInventory extends RecyclerView.Adapter<AdapterInventory.RHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    final Calendar myCalender = Calendar.getInstance();
    String m, d;

    List<DataInventoryMgt> rvInventoryData;
    ItemInventoryBinding binding;
    Context mContext;

    InventoryActivity inventoryActivity = new InventoryActivity();

    public AdapterInventory(List<DataInventoryMgt> rvInventoryData, Context mContext) {
        this.rvInventoryData = rvInventoryData;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public RHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new RHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RHolder holder, int position) {
        holder.product.setText(rvInventoryData.get(position).getProduct());
        holder.unitPrice.setText(String.format("kES %s", rvInventoryData.get(position).getUnitPrice()));
        holder.units.setText(rvInventoryData.get(position).getUnitOfMeasure());
        holder.reOrderLevel.setText(rvInventoryData.get(position).getReOrderLevel());
        holder.quantity.setText(rvInventoryData.get(position).getStockQuantity());
        holder.purchaseDate.setText(rvInventoryData.get(position).getPurhaseDate());
        holder.expiryDate.setText(rvInventoryData.get(position).getExpiryDate());
        holder.inventoryEdit.setOnClickListener(v -> {
            String iId = rvInventoryData.get(position).getId();
            String mId = rvInventoryData.get(position).getMerchantId();
            String pId = rvInventoryData.get(position).getProductId();
            String cId = rvInventoryData.get(position).getCategoryId();
            String txt1 = rvInventoryData.get(position).getUnitPrice();
            String txt2 = rvInventoryData.get(position).getReOrderLevel();
            String txt3 = rvInventoryData.get(position).getPurhaseDate();
            String txt4 = rvInventoryData.get(position).getExpiryDate();
            String createdBy = rvInventoryData.get(position).getCreatedById();

            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenI = preferences.getString(KEY_TOKEN, "Token");

            // start dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_inventory);
            dialog.setCancelable(false);

            EditText price = (EditText) dialog.findViewById(R.id.unitPrice);
            EditText reOrder = (EditText) dialog.findViewById(R.id.reOrderLevel);
            Spinner unitMeasure = (Spinner) dialog.findViewById(R.id.unitOfMeasure);
            EditText purchaseDate = (EditText) dialog.findViewById(R.id.inventoryPurchaseD);
            EditText expiryDate = (EditText) dialog.findViewById(R.id.inventoryExpiryD);
            AppCompatButton svBtn = (AppCompatButton) dialog.findViewById(R.id.saveBtn);

            price.setText(txt1);
            reOrder.setText(txt2);
//            purchaseDate.setText(txt3);
//            expiryDate.setText(txt4);

            String[] unitM = {"Select...", "Kgs", "gs", "Litres", "Tonnes"};
            ArrayAdapter<String> adapterUnit = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line, unitM);
            unitMeasure.setAdapter(adapterUnit);

            purchaseDate.setOnClickListener(v12 -> {
                int mYear = myCalender.get(Calendar.YEAR);
                int mMonth = myCalender.get(Calendar.MONTH);
                int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                        (view, year, month, dayOfMonth) -> {
                            myCalender.set(year, month, dayOfMonth);
                            if(month < 10){
                                m ="0" + (month + 1);
                            } else {
                                m = ""+ (month + 1);
                            }
                            if (dayOfMonth<10){
                                d ="0"+ dayOfMonth;
                            } else {
                                d = "" + dayOfMonth;
                            }
                            String fnD = year + "-" + m + "-" + d;
                            purchaseDate.setText(fnD);
                        },mYear, mMonth, mDay);
                datePickerDialog.show();
            });

            expiryDate.setOnClickListener(v13 -> {
                int mYear = myCalender.get(Calendar.YEAR);
                int mMonth = myCalender.get(Calendar.MONTH);
                int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                        (view, year, month, dayOfMonth) -> {
                            myCalender.set(year, month, dayOfMonth);
                            if(month < 10){
                                m ="0" + (month + 1);
                            } else {
                                m = ""+ (month + 1);
                            }
                            if (dayOfMonth<10){
                                d ="0"+ dayOfMonth;
                            } else {
                                d = "" + dayOfMonth;
                            }
                            String enD = year + "-" + m + "-" + d;
                            expiryDate.setText(enD);
                        },mYear, mMonth, mDay);
                datePickerDialog.show();
            });

            svBtn.setOnClickListener(v1 -> {
                String iUp = price.getText().toString().trim();
                String iRl = reOrder.getText().toString().trim();
                String iUm = unitMeasure.getSelectedItem().toString().trim();
                String iPd = purchaseDate.getText().toString().trim();
                String iEd = expiryDate.getText().toString().trim();

                Log.d("data", iId + mId + cId + pId + " " + createdBy + " " + tokenI);
                inventoryActivity.modifyInventory(iId, mId, pId, cId, iUm, iUp, iRl, iEd, iPd, mContext, createdBy, tokenI);
                dialog.dismiss();
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        });
    }

    @Override
    public int getItemCount() {
        return rvInventoryData.size();
    }

    public static class RHolder extends RecyclerView.ViewHolder {

        TextView product, units, reOrderLevel, quantity, categoryName, supplierName,
                unitPrice, purchaseDate, expiryDate;
        FloatingActionButton inventoryEdit;

        public RHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ItemInventoryBinding itemInventoryBinding = ItemInventoryBinding.bind(itemView);
            product = itemInventoryBinding.pName;
            units = itemInventoryBinding.pUnits;
            reOrderLevel = itemInventoryBinding.pReorderLevel;
            quantity = itemInventoryBinding.quantity;
            categoryName = itemInventoryBinding.pCategoryName;
            supplierName = itemInventoryBinding.pSupplier;
            purchaseDate = itemInventoryBinding.purchaseDate;
            expiryDate = itemInventoryBinding.expiryDate;
            unitPrice = itemInventoryBinding.unitPrice;
            inventoryEdit = itemInventoryBinding.prodEdit;
        }
    }
}
