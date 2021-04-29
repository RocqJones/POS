package com.intoverflown.pos.api_response

data class LoginResponse(
    val Id: String?,
    val FirstName: String?,
    val LastName: String?,
    val Username: String?,
    val Role: String?,
    val Token: String?
)