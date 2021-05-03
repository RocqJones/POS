package com.intoverflown.pos.ui.inventory.addproduct;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.intoverflown.pos.databinding.ActivityAddProductBinding;
import com.intoverflown.pos.ui.inventory.InventoryActivityMain;
import com.intoverflown.pos.utils.Constants;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBackBtn.setOnClickListener(v -> {
            Intent i = new  Intent(AddProductActivity.this, InventoryActivityMain.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        binding.addSaveBtn.setOnClickListener(v -> {
            String url = Constants.BASE_URL + "Product/Create";
            postProduct(url);
        });
    }

    private void postProduct(String url) { }
}
