package com.extrainch.pos.ui.printer.addpurchasereceipt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.extrainch.pos.MainActivity;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityAddPurchaseReceiptBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.category.add.AddCategoryActivity;
import com.extrainch.pos.ui.products.add.AddProductActivity;
import com.extrainch.pos.ui.purchase.add.AddPurchaseOrderActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPurchaseReceiptActivity extends AppCompatActivity {

    private ActivityAddPurchaseReceiptBinding binding;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";
    public String KEY_ID = "Id";
    public String PURCHASE_ID = "purchaseId";

    public String CATEGORY_ARR = "categoryArr";
    public String PRODUCT_ARR = "productArr";
    String productId;
    String categoryId;
    String uid;
    String token;
    Integer purchaseId;

    final Calendar myCalender = Calendar.getInstance();
    String m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPurchaseReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");

        if (preferences.contains("purchaseId")) {
            purchaseId = Integer.valueOf(preferences.getString(PURCHASE_ID, "purchaseId"));
        } else {
            Intent pO = new Intent(this, AddPurchaseOrderActivity.class);
            startActivity(pO);
        }

        String[] payStatus = {"Select...", "Paid", "Unpaid", "Quote", "Prepaid", "Postpaid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, payStatus);
        binding.paymentStatus.setAdapter(adapter);

        if (preferences.contains("productArr")) {
            String supplierArr = preferences.getString(PRODUCT_ARR, "productArr");

            String temp_str_p = supplierArr.replace("[", "").replace("]", "");
            String[] supplierT = temp_str_p.split(",");
            ArrayAdapter<String> adapterP = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, supplierT);
            binding.selectProduct.setAdapter(adapterP);

            String sp = binding.selectProduct.getSelectedItem().toString().trim();
            String [] s = sp.split(":");
            productId = s[0];
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create purchase receipt you must maintain a product!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent p = new Intent(this, AddProductActivity.class);
                startActivity(p);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        if (preferences.contains("categoryArr")) {
            String categoryArr = preferences.getString(CATEGORY_ARR, "categoryArr");

            String temp_str = categoryArr.replace("[", "").replace("]", "");
            String[] categoryT = temp_str.split(",");
            ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, categoryT);
            binding.purchaseOrderCategory.setAdapter(adapterC);

            String ct = binding.purchaseOrderCategory.getSelectedItem().toString().trim();
            String [] c = ct.split(":");
            categoryId = c[0];
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
            Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

            warnMessage.setText("To create purchase receipt you must create an product category!");

            okBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent cT = new Intent(this, AddCategoryActivity.class);
                startActivity(cT);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        }

        binding.receivedDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
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
                binding.receivedDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.amountDueDate.setOnClickListener(v -> {
            int mYear = myCalender.get(Calendar.YEAR);
            int mMonth = myCalender.get(Calendar.MONTH);
            int mDay = myCalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
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
                binding.amountDueDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.createReceipt.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "PurchaseReceipt/Create";
            postPurchaseReceipt(url);
        });
    }

    private void postPurchaseReceipt(String url) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        String orderQ = binding.purchaseQuantity.getText().toString().trim();
        String receivedQ = binding.receivedQuantity.getText().toString().trim();
        String dateRvd = binding.receivedDate.getText().toString().trim();
        String payStatus = binding.paymentStatus.getSelectedItem().toString().trim();
        String amountPaid = binding.amountPaid.getText().toString().trim();
        String amountDue = binding.amountDue.getText().toString().trim();
        String dueDate = binding.amountDueDate.getText().toString().trim();
        String remarks = binding.remarks.getText().toString().trim();

        if (orderQ.isEmpty() || receivedQ.isEmpty() || dateRvd.isEmpty() || amountPaid.isEmpty()
                || amountDue.isEmpty() || dueDate.isEmpty() || remarks.isEmpty()) {
            String empError = "Sorry, Please fill all the fields required!";
            warnDialog(empError);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating receipt...");
            progressDialog.show();

            try {
                jsonObject.put("purchaseId", purchaseId);
                jsonObject.put("productId", productId);
                jsonObject.put("categoryId", categoryId);
                jsonObject.put("orderedQuantity", orderQ);
                jsonObject.put("receivedQuantity", receivedQ);
                jsonObject.put("dateReceived", dateRvd);
                jsonObject.put("paymentStatusId", payStatus);
                jsonObject.put("amountPaid", amountPaid);
                jsonObject.put("amountDue", amountDue);
                jsonObject.put("paymentDueDate", dueDate);
                jsonObject.put("remarks", remarks);
                jsonObject.put("createdById", uid);

                jsonArray = new JSONArray("["+jsonObject.toString()+"]");
                Log.d("postR", jsonArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, url,
                    jsonArray, response -> {
                try {
                    Log.d("response receipt", response.toString());

                    String mes = null;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("id", jsonObj.optString("uniqueId"));
                        mes = jsonObj.optString("description");

//                        editor = preferences.edit();
//                        editor.putString("orderId", jsonObj.optString("uniqueId"));
//                        editor.apply();
                    }
                    progressDialog.dismiss();
                    String successMessage = "Created Successfully!";
                    successDialog(successMessage);
                    warnDialog(mes);
                    //Toast.makeText(this, "Created Successfully!", Toast.LENGTH_SHORT).show();
                    // to the next screen
                } catch (Exception e) {
                    e.printStackTrace();
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            }, error -> {
                error.printStackTrace();
                progressDialog.dismiss();
                //Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                String failed = "The server was unreachable, check your internet connection!";
                warnDialog(failed);
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + token);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        }
    }

    private void successDialog(String successM) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView successMessage = (TextView) dialog.findViewById(R.id.successMessage);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        successMessage.setText(successM);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    private void warnDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(false);

        TextView warnMessage = (TextView) dialog.findViewById(R.id.warnMessage);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        warnMessage.setText(message);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }
}