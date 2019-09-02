package com.wmontgom85.personsreponative.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@Entity
class Person {
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0L
    var gender: String? = null
    var email: String? = null
    var phone: String? = null
    var cell: String? = null
    var thumbnail: String? = null
    var firstName : String? = null
    var lastName : String? = null
    var street : String? = null
    var city : String? = null
    var state : String? = null
    var postcode : String? = null
    var birthdate : String? = null
    var avatarLarge : String? = null
    var avatarMedium : String? = null
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var image_blob : ByteArray? = null

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

    fun setImage(img: Bitmap) {
        try {
            val stream = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.PNG, 100, stream)
            this.image_blob = stream.toByteArray()
        } catch (tx: Throwable) {
            this.image_blob = null
        }
    }

    fun getImage() : Bitmap? {
        image_blob?.let {
            try {
                return BitmapFactory.decodeByteArray(it,0, it.size)
            } catch (tx: Throwable) {}
        }
        return null
    }

    fun readPerson(obj : JSONObject) {

    }
}