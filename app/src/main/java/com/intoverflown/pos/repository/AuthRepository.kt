package com.intoverflown.pos.repository

import com.intoverflown.pos.network.ApiInterface

// Extend Base Repository
class AuthRepository(private val api : ApiInterface) : BaseRepository() {

    // login
    suspend fun login(username: String, password : String) = safeApiCall {
        api.login(username, password)
    }
}