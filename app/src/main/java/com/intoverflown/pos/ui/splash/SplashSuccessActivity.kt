package com.intoverflown.pos.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intoverflown.pos.databinding.ActivitySplashSuccessBinding

class SplashSuccessActivity : AppCompatActivity() {

    private var binding : ActivitySplashSuccessBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashSuccessBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}