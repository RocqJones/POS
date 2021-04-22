package com.intoverflown.pos.ui.registerbranch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intoverflown.pos.MainActivity
import com.intoverflown.pos.databinding.ActivityNewBranchBinding

class NewBranchActivity : AppCompatActivity() {

    private var binding: ActivityNewBranchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBranchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.upBtn.setOnClickListener {
            val x = Intent(this, MainActivity::class.java)
            x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(x)
        }
    }
}