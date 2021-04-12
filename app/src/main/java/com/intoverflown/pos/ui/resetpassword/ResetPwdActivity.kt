package com.intoverflown.pos.ui.resetpassword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.intoverflown.pos.databinding.ActivityResetPwdBinding
import com.intoverflown.pos.ui.signin.LoginActivity

class ResetPwdActivity : AppCompatActivity() {
    // binding
    private var binding : ActivityResetPwdBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPwdBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.resetPwdBackBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}