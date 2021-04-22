package com.intoverflown.pos.ui.inventory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.intoverflown.pos.databinding.ActivityInventoryMainBinding
import com.intoverflown.pos.ui.inventory.addproduct.AddProductActivity

class InventoryActivityMain : AppCompatActivity() {

    private var binding : ActivityInventoryMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.fab.setOnClickListener {
            val i = Intent(this, AddProductActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
    }
}