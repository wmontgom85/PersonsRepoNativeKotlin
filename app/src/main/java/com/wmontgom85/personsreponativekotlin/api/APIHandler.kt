package com.wmontgom85.personsreponativekotlin.api

import com.wmontgom85.personsreponativekotlin.sealed.APIResult

import android.util.Log

/**
 * Module which provides all required dependencies about network
 */
object APIHandler {
    fun apiCall(task: APITask, request: RESTRequest): APIResult<Any> {
        val result: APIResult<Any> = ConnectionProvider(task, request).makeRequest()

        when (result) {
            is APIResult.Success -> {
                try {
                    // attempt parsing and set data as object
                    task.jsonAdapter.readJson(result.data as String)?.let {
                        return APIResult.Success(it)
                    } ?: run {
                        // parsing error
                        return APIResult.Error(Exception("An error has occurred while parsing. Error code AH001"))
                    }
                } catch (tx : Throwable) {
                    Log.d("1.APIHandler", tx.message)

                    // fatal error
                    return APIResult.Error(Exception("An error has occurred while parsing. Error code AH002"))
                }

            }
        }

        return result
    }
}




