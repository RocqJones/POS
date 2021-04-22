package com.intoverflown.pos.ui.inventory.addproduct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intoverflown.pos.databinding.ActivityAddProductBinding
import com.intoverflown.pos.ui.inventory.InventoryActivityMain

class AddProductActivity : AppCompatActivity() {

    private var binding : ActivityAddProductBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.addBackBtn.setOnClickListener {
            val i = Intent(this, InventoryActivityMain::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
    }
}