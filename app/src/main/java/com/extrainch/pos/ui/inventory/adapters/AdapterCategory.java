package com.extrainch.pos.ui.inventory.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.pos.R;
import com.extrainch.pos.databinding.ItemCategoryBinding;
import com.extrainch.pos.ui.inventory.data.InventoryRemoteData;
import com.extrainch.pos.ui.inventory.fragments.CategoryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.RvHolder> {

    public SharedPreferences preferences;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_TOKEN = "Token";

    List<InventoryRemoteData> categoryData;
    ItemCategoryBinding binding;
    Context mContext;

    CategoryFragment categoryFragment = new CategoryFragment();

    public AdapterCategory(List<InventoryRemoteData> categoryData, Context mContext) {
        this.categoryData = categoryData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()));
        View v = binding.getRoot();
        return new RvHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RvHolder holder, int position) {
        holder.cName.setText(categoryData.get(position).getName());
        holder.cRemarks.setText(categoryData.get(position).getRemarks());
        holder.cDate.setText(categoryData.get(position).getDateCreated());
        holder.cEdit.setOnClickListener(v -> {
            String cId = categoryData.get(position).getItemCatId();
            String txt1 = categoryData.get(position).getName();
            String txt2 = categoryData.get(position).getRemarks();
            String createdBy = categoryData.get(position).getCreatedById();
            preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            String tokenM = preferences.getString(KEY_TOKEN, "Token");
            Log.d("editCat", cId + ", " + txt1 + ", " + txt2 + createdBy);
            Log.d("editToken", tokenM);

            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.edit_category);
            dialog.setCancelable(false);

            EditText catName = (EditText) dialog.findViewById(R.id.editCategoryName);
            EditText remarks = (EditText) dialog.findViewById(R.id.editRemarks);
            Button svBtn = (Button) dialog.findViewById(R.id.saveBtn);

            catName.setText(txt1);
            remarks.setText(txt2);

            svBtn.setOnClickListener(v1 -> {
                dialog.dismiss();
                String name = catName.getText().toString().trim();
                String rmk = remarks.getText().toString().trim();
                categoryFragment.modifyData(cId, name, rmk, createdBy, tokenM);
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_1;
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
        });
    }

    @Override
    public int getItemCount() {
        return categoryData.size();
    }

    static public class RvHolder extends RecyclerView.ViewHolder {

        TextView cName, cDate, cRemarks;
        FloatingActionButton cEdit;

        public RvHolder(@NonNull View itemView) {
            super(itemView);
            ItemCategoryBinding itemCategoryBinding = ItemCategoryBinding.bind(itemView);
            cName = itemCategoryBinding.cName;
            cDate = itemCategoryBinding.dateCreated;
            cRemarks = itemCategoryBinding.cRemarks;
            cEdit = itemCategoryBinding.editCategory;
        }
    }
}
