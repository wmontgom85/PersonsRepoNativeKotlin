package com.wmontgom85.personsreponative.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@Entity
data class Person(
    val gender: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val cell: String? = null,
    val thumbnail: String? = null,
    val firstName : String? = null,
    val lastName : String? = null,
    val street : String? = null,
    val city : String? = null,
    val state : String? = null,
    val postcode : String? = null,
    val birthdate : String? = null,
    val avatarLarge : String? = null,
    val avatarMedium : String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0L

    fun buildAddress() : String {
        var line1 = ""
        var line2 = ""

        street?.let { s -> line1 = s.split(' ').joinToString(" ") { it.capitalize() } }

        city?.let {
            line2 = it.capitalize()
        }

        state?.let {
            line2 += when {
                line2.isNotEmpty() -> ", ${it.capitalize()}"
                else -> it.capitalize()
            }
        }

        postcode?.let {
            line2 += when {
                line2.isNotEmpty() -> ", $it"
                else -> it
            }
        }

        return when {
            (line1.isNotEmpty() && line2.isNotEmpty()) -> "$line1 \n$line2"
            line1.isNotEmpty() -> line1
            line2.isNotEmpty() -> line2
            else -> ""
        }
    }
}