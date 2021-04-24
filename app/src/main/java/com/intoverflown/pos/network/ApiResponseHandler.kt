package com.intoverflown.pos.network

import okhttp3.ResponseBody

/** Generic class that handle both success and failure either by network or api
 * We will use this sealed class to wrap or handle success and errors properly
 * Make sure to extend Failure class with "ApiResponseHandler" to return 'Nothing' */
sealed class ApiResponseHandler<out T> {
    data class Success<out T>(val value: T) : ApiResponseHandler<T>()

    data class Failure(
        val isNetworkError : Boolean,
        val errorCode : Int?,
        val errorBody: ResponseBody?
    ) : ApiResponseHandler<Nothing>()
}
