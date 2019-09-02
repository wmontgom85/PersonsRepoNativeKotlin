package com.wmontgom85.personsreponativekotlin.api

import com.wmontgom85.personsreponativekotlin.api.jsonadapter.JsonAdapter

data class APITask(
        val jsonAdapter : JsonAdapter,
        val successMessage : String? = null,
        val errorMessage : String? = null
)