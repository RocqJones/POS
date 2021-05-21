package com.intoverflown.pos.ui.inventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.intoverflown.pos.databinding.ItemCategoryBinding;
import com.intoverflown.pos.ui.inventory.data.InventoryRemoteData;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.RvHolder> {

    List<InventoryRemoteData> categoryData;
    ItemCategoryBinding binding;
    Context mContext;

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
    }

    @Override
    public int getItemCount() {
        return categoryData.size();
    }

    static public class RvHolder extends RecyclerView.ViewHolder {

        TextView cName, cDate, cRemarks;
        public RvHolder(@NonNull View itemView) {
            super(itemView);
            ItemCategoryBinding itemCategoryBinding = ItemCategoryBinding.bind(itemView);
            cName = itemCategoryBinding.cName;
            cDate = itemCategoryBinding.dateCreated;
            cRemarks = itemCategoryBinding.cRemarks;
        }
    }
}
