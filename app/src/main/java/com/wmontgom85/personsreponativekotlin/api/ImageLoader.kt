package com.wmontgom85.personsreponativekotlin.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class ImageLoader {
    private var url : String? = ""

    //create a new Job
    private val parentJob = Job()

    //create a coroutine context with the job and the dispatcher
    private val coroutineContext : CoroutineContext get() = parentJob + Dispatchers.IO

    //create a coroutine scope with the coroutine context
    private val scope = CoroutineScope(coroutineContext)

    fun get(url: String) : ImageLoader {
        this.url = url
        return this
    }

    fun loadInto(v: ImageView) {
        scope.launch {
            var bitmap : Bitmap? = null

            try {
                val inputStream = URL(url).openConnection().getInputStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (tx: Throwable) {
                Log.d("1.ImageLoader", "Message: $tx.message")
            }

            launch(Dispatchers.Main) {
                bitmap?.let {
                    v.setImageBitmap(it)
                } ?: run {
                    // @TODO set placeholder?
                }
            }
        }
    }

    fun cancel() {
        scope.cancel()
    }
}