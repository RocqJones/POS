package com.intoverflown.pos.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intoverflown.pos.repository.AuthRepository
import com.intoverflown.pos.repository.BaseRepository
import com.intoverflown.pos.ui.signin.LoginViewModel

class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            // def all models here
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(repository as AuthRepository) as T

            else -> throw IllegalArgumentException("viewModelClass Not Found")
        }
    }
}