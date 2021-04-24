package com.intoverflown.pos.repository

import com.intoverflown.pos.network.ApiResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall : suspend () -> T
    ) : ApiResponseHandler<T> {
        // execute all api calls in IO dispatcher
        return withContext(Dispatchers.IO) {
            // check api calls
            try {
                // if successful we'll get results directly and put them in our ApiResponseHandler
                ApiResponseHandler.Success(apiCall.invoke())
            } catch (throwable : Throwable) {
                when (throwable) {
                    // api error
                    is HttpException -> {
                        ApiResponseHandler.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    // considered as network error
                    else -> {
                        ApiResponseHandler.Failure(true, null, null)
                    }
                }
            }
        }
    }
}