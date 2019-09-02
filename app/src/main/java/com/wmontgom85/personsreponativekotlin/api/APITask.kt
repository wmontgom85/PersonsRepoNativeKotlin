package com.wmontgom85.personsreponative.api

import com.wmontgom85.personsreponative.api.jsonadapter.JsonAdapter

data class APITask(
        val jsonAdapter : JsonAdapter,
        val successMessage : String? = null,
        val errorMessage : String? = null
)