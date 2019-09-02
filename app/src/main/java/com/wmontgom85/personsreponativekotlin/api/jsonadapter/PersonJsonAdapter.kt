package com.wmontgom85.personsreponativekotlin.api.jsonadapter

import android.util.Log
import com.wmontgom85.personsreponativekotlin.model.Person
import org.json.JSONArray
import org.json.JSONObject

class PersonJsonAdapter : JsonAdapter {
    override fun readJson(json: String): Any?{
        try {
            val res = JSONObject(json)

            if (res.has("results") && res.get("results") is JSONArray) {
                val obj = res.getJSONArray("results");
                val pObj = obj.getJSONObject(0)

                // create optional vals
                var firstName: String? = null
                var lastName: String? = null
                var street: String? = null
                var city: String? = null
                var state: String? = null
                var zip: String? = null
                var gender: String? = null
                var email: String? = null
                var phone: String? = null
                var cell: String? = null
                var birthdate: String? = null
                var avatarLarge: String? = null
                var avatarMedium: String? = null
                var thumbnail: String? = null

                if (pObj.has("name")) {
                    val name = pObj.getJSONObject("name")

                    if (name.has("first")) firstName = name.getString("first")
                    if (name.has("last")) lastName = name.getString("last")
                }

                if (pObj.has("location")) {
                    val location = pObj.getJSONObject("location")

                    if (location.has("street")) street = location.getString("street")
                    if (location.has("city")) city = location.getString("city")
                    if (location.has("state")) state = location.getString("state")
                    if (location.has("postcode")) zip = location.getString("postcode")
                }

                if (pObj.has("gender")) gender = pObj.getString("gender")
                if (pObj.has("email")) email = pObj.getString("email")
                if (pObj.has("phone")) phone = pObj.getString("phone")
                if (pObj.has("cell")) cell = pObj.getString("cell")

                if (pObj.has("dob")) {
                    val dob = pObj.getJSONObject("dob")

                    if (dob.has("date")) birthdate = dob.getString("date")
                }

                if (pObj.has("picture")) {
                    val picture = pObj.getJSONObject("picture")

                    if (picture.has("large")) avatarLarge = picture.getString("large")
                    if (picture.has("medium")) avatarMedium = picture.getString("medium")
                    if (picture.has("thumbnail")) thumbnail = picture.getString("thumbnail")
                }

                return Person(
                    gender,
                    email,
                    phone,
                    cell,
                    thumbnail,
                    firstName,
                    lastName,
                    street,
                    city,
                    state,
                    zip,
                    birthdate,
                    avatarLarge,
                    avatarMedium
                )
            }
        } catch (tx: Throwable) {
            Log.d("1.PersonJsonAdapter", "Error parsing. ${tx.message}")
        }

        return null
    }
}