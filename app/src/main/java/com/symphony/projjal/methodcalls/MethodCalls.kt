package com.symphony.projjal.methodcalls

import android.content.Context
import android.content.ContextWrapper
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object MethodCalls {
    operator fun get(urlString: String): String? {
        val stringBuilder = StringBuilder()
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 500
            val statusCode = connection.responseCode
            val inputStream: InputStream
            inputStream = if (statusCode >= 200 && statusCode < 400) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    private fun getResponseFromCache(context: Context, url: String): String? {
        var reader: BufferedReader? = null
        return try {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("SymphonyResponse", Context.MODE_PRIVATE)
            val myFile = File(directory, url.hashCode().toString() + ".txt")
            if (myFile.exists()) {
                reader = BufferedReader(FileReader(myFile))
                val textBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    textBuilder.append(line)
                    textBuilder.append("\n")
                }
                textBuilder.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}