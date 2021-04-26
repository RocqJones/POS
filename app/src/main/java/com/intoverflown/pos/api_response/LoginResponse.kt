package com.intoverflown.pos.api_response

data class LoginResponse(
    val FirstName: String,
    val Id: String,
    val LastName: String,
    val Role: String,
    val Token: String,
    val Username: String
)