package com.intoverflown.pos.api_responses

data class User(
    val Id : Int,
    val Username : String,
    val FirstName : String,
    val LastName : String,
    val Token : String,
    val Role : String,
)