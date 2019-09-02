package com.wmontgom85.personsreponative.api.jsonadapter

import com.wmontgom85.personsreponative.model.Person
import org.json.JSONArray
import org.json.JSONObject

class PersonJsonAdapter : JsonAdapter {
    override fun readJson(json: String): Any?{
        val res = JSONObject(json)

        if (res.has("results") && res.get("results") is JSONArray) {
            val obj = res.getJSONArray("results");
            val pObj = obj.getJSONObject(0)

            var p = Person()

            if (pObj.has("name")) {
                val name = pObj.getJSONObject("name")

                if (name.has("first")) p.firstName = name.getString("first")
                if (name.has("last")) p.firstName = name.getString("last")
            }

            if (pObj.has("location")) {
                val location = pObj.getJSONObject("location")

                if (location.has("street")) p.street = location.getString("street")
                if (location.has("city")) p.city = location.getString("city")
                if (location.has("state")) p.state = location.getString("state")
                if (location.has("postcode")) p.postcode = location.getString("postcode")
            }

            if (pObj.has("gender")) p.gender = pObj.getString("gender")
            if (pObj.has("email")) p.email = pObj.getString("email")
            if (pObj.has("phone")) p.phone = pObj.getString("phone")
            if (pObj.has("cell")) p.cell = pObj.getString("cell")

            if (pObj.has("dob")) {
                val dob = pObj.getJSONObject("dob")

                if (dob.has("date")) p.birthdate = dob.getString("date")
            }

            if (pObj.has("picture")) {
                val picture = pObj.getJSONObject("picture")

                if (picture.has("large")) p.avatarLarge = picture.getString("large")
                if (picture.has("medium")) p.avatarMedium = picture.getString("medium")
                if (picture.has("thumbnail")) p.thumbnail = picture.getString("thumbnail")
            }

            return p
        }

        return null
    }
}