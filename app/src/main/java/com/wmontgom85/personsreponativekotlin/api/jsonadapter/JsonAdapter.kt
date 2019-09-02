package com.wmontgom85.personsreponative.api.jsonadapter

interface JsonAdapter {
    fun readJson(json: String) : Any?
}