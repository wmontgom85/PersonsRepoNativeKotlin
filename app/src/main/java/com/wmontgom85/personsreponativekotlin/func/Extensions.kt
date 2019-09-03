package com.wmontgom85.personsreponativekotlin.func

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.coroutines.coroutineContext

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun View.newWidth(w: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = w
    this.layoutParams = layoutParams
}

internal fun View.newHeight(h: Int) {
    val layoutParams = this.layoutParams
    layoutParams.height = h
    this.layoutParams = layoutParams
}

internal fun Int.dp(): Int {
    return (this / Resources.getSystem().displayMetrics.density).toInt()
}

internal fun Int.px(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}