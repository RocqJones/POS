package com.intoverflown.pos.ui.registerbranch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intoverflown.pos.databinding.ActivityNewBranchBinding

class NewBranchActivity : AppCompatActivity() {

    private var binding: ActivityNewBranchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBranchBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}