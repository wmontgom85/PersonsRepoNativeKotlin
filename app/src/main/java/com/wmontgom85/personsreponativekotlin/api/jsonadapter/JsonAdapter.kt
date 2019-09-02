package com.wmontgom85.personsreponativekotlin.api.jsonadapter

interface JsonAdapter {
    fun readJson(json: String) : Any?
}