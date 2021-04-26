package com.intoverflown.pos.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intoverflown.pos.api_responses.LoginResponse
import com.intoverflown.pos.network.ApiResponseHandler
import com.intoverflown.pos.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResponse  : MutableLiveData<ApiResponseHandler<LoginResponse>> = MutableLiveData()
    val loginResponse : LiveData<ApiResponseHandler<LoginResponse>> get() = _loginResponse

    fun login(
        username : String,
        password : String
    ) = viewModelScope.launch {
        _loginResponse.value = repository.login(username, password)
    }
}