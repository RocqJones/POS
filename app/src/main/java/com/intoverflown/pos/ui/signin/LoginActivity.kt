package com.intoverflown.pos.ui.signin

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.intoverflown.pos.databinding.ActivityLoginBinding
import com.intoverflown.pos.network.ApiClient
import com.intoverflown.pos.network.ApiInterface
import com.intoverflown.pos.network.ApiResponseHandler
import com.intoverflown.pos.repository.AuthRepository
import com.intoverflown.pos.ui.base.BaseActivity
import com.intoverflown.pos.ui.base.ViewModelFactory
import com.intoverflown.pos.ui.resetpassword.ResetPwdActivity


class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // initialize VM
        viewModel =ViewModelProvider(this)[LoginViewModel::class.java]

//        binding!!.loginNavToSignUp.setOnClickListener {
//            val intent = Intent(this, SignUpActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
//        }
        binding!!.loginForgotPwd.setOnClickListener {
            val intent = Intent(this, ResetPwdActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // loginResponse from VM
        viewModel.loginResponse.observe(this, Observer {
            when (it) {
                is ApiResponseHandler.Success -> {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }

                is ApiResponseHandler.Failure -> {
                    Toast.makeText(this, "Login failure", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // pass factory here
        val apiClient = ApiClient()
        val repository = AuthRepository(apiClient.buildApi(ApiInterface::class.java))
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        setUpLogin()
    }

    private fun setUpLogin() {
        binding!!.loginBtn.setOnClickListener {
            val username = binding!!.loginUserId.text.toString().trim()
            val password = binding!!.loginPassword.text.toString().trim()

            // @TODO - Add input validations
            viewModel.login(username, password)
        }
    }

    // private fun getViewModel() = LoginViewModel::class.java

    // private fun getActivityRepository() = AuthRepository(apiClient.buildApi(ApiInterface::class.java))
}