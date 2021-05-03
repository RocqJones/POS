package com.intoverflown.pos.ui.inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.intoverflown.pos.databinding.ActivityInventoryMainBinding;
import com.intoverflown.pos.ui.inventory.addproduct.AddProductActivity;

public class InventoryActivityMain extends AppCompatActivity {

    private ActivityInventoryMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInventoryMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(v -> {
            Intent i = new Intent(InventoryActivityMain.this, AddProductActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });
    }
}
