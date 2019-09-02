package com.wmontgom85.personsreponative.api

import android.net.Uri

class RESTRequest {
    val baseUrl : String by lazy { "https://randomuser.me/api/1.2/" }

    var requestType : String = "GET"

    var endPoint : String = ""

    var params : Map<String, String>? = HashMap()

    val restURL : String by lazy { baseUrl + endPoint }

    var timeout : Int = 5000

    fun buildQuery() : String? {
        try {
            var builder = Uri.Builder()

            params?.let {
                it.forEach {
                    builder.appendQueryParameter(it.key, it.value)
                }
            }

            return builder.build().encodedQuery
        } catch (tx: Throwable) {}

        return null
    }
}