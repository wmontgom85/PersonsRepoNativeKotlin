package com.wmontgom85.personsreponative.api

import android.util.Log
import com.wmontgom85.personsreponative.sealed.APIResult
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Module which provides all required dependencies about network
 */
class ConnectionProvider(
        private val task : APITask,
        private val request : RESTRequest
) {
    /**
     * Makes an HTTP request with a provided RESTRequest object
     */
    fun <T : Any> makeRequest(): APIResult<T> {
        var response = ""

        var connection : HttpURLConnection? = null

        try {
            val url = URL(request.restURL)

            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                connectTimeout = request.timeout
                readTimeout = request.timeout
                requestMethod = request.requestType
                doInput = true
                doOutput = request.requestType == "POST"
                setRequestProperty("charset", "utf-8")

                if (request.requestType == "POST") {
                    request.buildQuery()?.let {
                        val os = outputStream
                        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8") as Writer)
                        writer.write(it)
                        writer.flush()
                        writer.close()
                        os.close()
                    }
                }
            }

            return when {
                connection.responseCode == HttpsURLConnection.HTTP_OK -> {
                    val br = BufferedReader(InputStreamReader(connection.inputStream))
                    br.readLine().forEach {
                        response += it
                    }
                    APIResult.Success(response)
                }
                else -> APIResult.Error(IOException(task.errorMessage?.let { "An error has occurred. Error code CP001" }))
            }
        } catch (tx: Throwable) {
            Log.d("1.ConnectionProvider", tx.message)

            // set the connection to null so no further execution can occur
            return APIResult.Error(IOException("${task.errorMessage?.let { "An error has occurred." }}. Error Code CP002"))
        } finally {
            connection?.let { it.disconnect() }
        }
    }
}