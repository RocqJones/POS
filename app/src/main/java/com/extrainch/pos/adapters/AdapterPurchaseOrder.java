package com.extrainch.pos.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.extrainch.pos.databinding.ItemPurchaseBinding;
import com.extrainch.pos.repository.PurchaseRemoteData;
import com.extrainch.pos.ui.purchase.PurchaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterPurchaseOrder extends RecyclerView.Adapter<AdapterPurchaseOrder.RvHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    final Calendar myCalender = Calendar.getInstance();
    String m, d;

    List<PurchaseRemoteData> rvPurchaseRemoteData;
    ItemPurchaseBinding binding;
    Context mContext;

    PurchaseActivity purchaseActivity = new PurchaseActivity();

    public AdapterPurchaseOrder(List<PurchaseRemoteData> rvPurchaseRemoteData, Context mContext) {
        this.rvPurchaseRemoteData = rvPurchaseRemoteData;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public AdapterPurchaseOrder.RvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPurchaseBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new RvHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPurchaseOrder.RvHolder holder, int position) {
        holder.product.setText(rvPurchaseRemoteData.get(position).getProduct());
        holder.supplier.setText(rvPurchaseRemoteData.get(position).getSupplier());
        holder.quantity.setText(rvPurchaseRemoteData.get(position).getQuantity());
        holder.unitPrice.setText(rvPurchaseRemoteData.get(position).getUnitCost());
        holder.totalCost.setText(rvPurchaseRemoteData.get(position).getTotalCost());
        holder.purchase.setText(rvPurchaseRemoteData.get(position).getPurchase());

        holder.editBtn.setOnClickListener(v -> {
            String holderId = rvPurchaseRemoteData.get(position).getId();
            String purchaseId = rvPurchaseRemoteData.get(position).getPurchaseId();
            String mId = rvPurchaseRemoteData.get(position).getMerchantId();
            String prodId = rvPurchaseRemoteData.get(position).getProductId();
            String sId = rvPurchaseRemoteData.get(position).getSupplierId();
            String createdBy = rvPurchaseRemoteData.get(position).getCreatedById();
            String txt1 = rvPurchaseRemoteData.get(position).getQuantity();
            String txt2 = rvPurchaseRemoteData.get(position).getUnitCost();

            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenPc = preferences.getString(KEY_TOKEN, "Token");

            // start mod dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_purchase);
            dialog.setCancelable(false);

            EditText requestedDate = (EditText) dialog.findViewById(R.id.requestedDatePur);
            EditText purchaseQuantity = (EditText) dialog.findViewById(R.id.purchaseQuantity);
            EditText unitCostP = (EditText) dialog.findViewById(R.id.unitCostP);
            EditText totalCostP = (EditText) dialog.findViewById(R.id.totalCostP);
            Spinner paymentTypeP = (Spinner) dialog.findViewById(R.id.paymentTypeP);
            Spinner statusP = (Spinner) dialog.findViewById(R.id.statusP);
            AppCompatButton svBtn = (AppCompatButton) dialog.findViewById(R.id.saveBtn);

            // set txt and watch
            purchaseQuantity.setText(txt1);
            unitCostP.setText(txt2);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!purchaseQuantity.getText().toString().equals("") &&
                            !unitCostP.getText().toString().equals("")) {
                        int quantity = Integer.parseInt(purchaseQuantity.getText().toString());
                        double unitCost = Double.parseDouble(unitCostP.getText().toString());

                        double results = unitCost * quantity;
                        totalCostP.setText(String.valueOf(results));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            };

            purchaseQuantity.addTextChangedListener(textWatcher);
            unitCostP.addTextChangedListener(textWatcher);

            requestedDate.setOnClickListener(v1 -> {
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
                            requestedDate.setText(fnD);
                        },mYear, mMonth, mDay);
                datePickerDialog.show();
            });

            String[] payT = {"Select...", "Cash", "Mobile", "Card"};
            ArrayAdapter<String> adapterPayT = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line, payT);
            paymentTypeP.setAdapter(adapterPayT);

            String[] pStatus = {"Select...", "Quote", "Paid", "Installment"};
            ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line, pStatus);
            statusP.setAdapter(adapterStatus);


            svBtn.setOnClickListener(v12 -> {
                String rqDate = requestedDate.getText().toString().trim();
                String qTy = purchaseQuantity.getText().toString().trim();
                String unitP = unitCostP.getText().toString().trim();
                String totalC = totalCostP.getText().toString().trim();
                String payTy = paymentTypeP.getSelectedItem().toString().trim();
                String pStatus1 = statusP.getSelectedItem().toString().trim();

                purchaseActivity.modifyPurchase(holderId, mId, sId, createdBy, purchaseId, prodId, mContext, rqDate, qTy,
                        unitP, totalC, payTy, pStatus1, tokenPc);
                dialog.dismiss();
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            // end dialog
        });
    }

    @Override
    public int getItemCount() {
        return rvPurchaseRemoteData.size();
    }

    public static class RvHolder extends RecyclerView.ViewHolder {

        TextView product, quantity, supplier, unitPrice, totalCost, purchase;
        FloatingActionButton editBtn;

        public RvHolder(@NonNull View itemView) {
            super(itemView);
            ItemPurchaseBinding itemPurchaseBinding = ItemPurchaseBinding.bind(itemView);
            product = itemPurchaseBinding.productName;
            quantity = itemPurchaseBinding.quantity;
            supplier = itemPurchaseBinding.supplierName;
            unitPrice = itemPurchaseBinding.unitPrice;
            totalCost = itemPurchaseBinding.totalCost;
            purchase = itemPurchaseBinding.purchase;
            editBtn = itemPurchaseBinding.edit;
        }
    }
}