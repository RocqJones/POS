package com.intoverflown.pos.ui.base

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.intoverflown.pos.network.ApiClient
import com.intoverflown.pos.repository.BaseRepository

abstract class BaseActivity<VM: ViewModel, R: BaseRepository> : Activity() {

    // An instance of remote data source will be called in "getActivityRepository()" in login
    protected val apiClient = ApiClient()

    abstract fun getViewModel() : Class<VM>

    abstract fun getActivityRepository() : R
}