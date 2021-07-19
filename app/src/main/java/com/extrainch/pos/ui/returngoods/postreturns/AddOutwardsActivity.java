package com.extrainch.pos.ui.returngoods.postreturns;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ActivityAddOutwardsBinding;
import com.extrainch.pos.patterns.MySingleton;
import com.extrainch.pos.ui.customers.addcustomers.AddCustomerActivity;
import com.extrainch.pos.ui.inventory.postdata.AddSupplierActivity;
import com.extrainch.pos.ui.merchantbranch.addmerchantbranch.NewBranchActivity;
import com.extrainch.pos.ui.returngoods.InwardsOutwardsActivity;
import com.extrainch.pos.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddOutwardsActivity extends AppCompatActivity {

    private ActivityAddOutwardsBinding binding;

    final Calendar myCalender = Calendar.getInstance();

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_TOKEN = "Token";
    public String SUPPLIER_ID = "supplierId";
    public String CUSTOMER_ID = "customerId";
    public String MERCHANT_BRANCH = "merchantBranchId";

    String uid;
    String token;
    String supplierId;
    Integer customerId;
    String merchantBranchId;

    String m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOutwardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, InwardsOutwardsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // order date picker
        binding.selectOrderDate.setOnClickListener(v -> {
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
                binding.selectOrderDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        // delivered date picker
        binding.selectDateDelivered.setOnClickListener(v -> {
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
                binding.selectDateDelivered.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        // returned date picker
        binding.selectReturnedDate.setOnClickListener(v -> {
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
                binding.selectReturnedDate.setText(fnD);
            },mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        preferences = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        uid = preferences.getString(KEY_ID, "Id");
        token = preferences.getString(KEY_TOKEN, "Token");
        if (preferences.contains("supplierId")) {
            supplierId = preferences.getString(SUPPLIER_ID, "supplierId");
        } else {
            Intent sP = new Intent(this, AddSupplierActivity.class);
            startActivity(sP);
        }

        if (preferences.contains("merchantBranchId")) {
            merchantBranchId = preferences.getString(MERCHANT_BRANCH, "merchantBranchId");
        } else {
            Intent mB = new Intent(this, NewBranchActivity.class);
            startActivity(mB);
        }

        if (preferences.contains("customerId")) {
            customerId = Integer.valueOf(preferences.getString(CUSTOMER_ID, "customerId"));
        } else {
            Intent cS = new Intent(this, AddCustomerActivity.class);
            startActivity(cS);
        }

        binding.createReturnOut.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "ReturnOutward/Create";
            createReturnOutwards(url);
        });
    }

    private void createReturnOutwards(String url) {
        String orderDate = binding.selectOrderDate.getText().toString().trim();
        int orderedQuantity = Integer.parseInt(binding.orderQuantity.getText().toString().trim());
        String dateDelivered = binding.selectDateDelivered.getText().toString().trim();
        int returnedQuantity = Integer.parseInt(binding.returnedQuantity.getText().toString().trim());
        String returnDate = binding.selectReturnedDate.getText().toString().trim();
        String returnReason = binding.returnedReason.getText().toString().trim();

        if (orderDate.isEmpty() || dateDelivered.isEmpty() || returnDate.isEmpty() || returnReason.isEmpty()) {
            String empError = "Sorry, Please fill all the required fields!";
            warnDialog(empError);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddOutwardsActivity.this);
            progressDialog.setMessage("Creating return outwards...");
            progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                jsonObject.put("merchantBranchId", merchantBranchId);
                jsonObject.put("supplierId", supplierId);
                jsonObject.put("customerId", customerId);
                jsonObject.put("orderDate", orderDate);
                jsonObject.put("orderedQuantity", orderedQuantity);
                jsonObject.put("dateDelivered", dateDelivered);
                jsonObject.put("returnedQuantity", returnedQuantity);
                jsonObject.put("returnDate", returnDate);
                jsonObject.put("returnReason", returnReason);
                jsonObject.put("createdById", uid);

                jsonArray = new JSONArray("["+jsonObject.toString()+"]");
                Log.d("post inwards", jsonArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                    url, jsonArray, response -> {
                try {
                    Log.d("response", response.toString());
                    progressDialog.dismiss();
                    String mg = "Return Outwards created successfully!";
                    successDialog(mg);
                    goBack();
                } catch (Exception e) {
                    e.printStackTrace();
                    String err = "Error occurred while sending request\nCheck logs!";
                    warnDialog(err);
                }
            }, error -> {
                error.printStackTrace();
                progressDialog.dismiss();
                String failed = "Failed because the server was unreachable, check your internet connection!";
                warnDialog(failed);
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + token);
                    params.put("Content-Type", "application/json");
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

    private void goBack() {
        Intent j = new Intent(this, InwardsOutwardsActivity.class);
        j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(j);
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