package com.mohamed.tahiri.termscanguardian.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

fun saveNameToFile(context: Context, name: String) {
    val fileName = "fullName.txt"
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use {
        it.write(name.toByteArray())
    }
}

fun getNameFromFile(context: Context): String {
    val fileName = "fullName.txt"
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        FileInputStream(file).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                reader.readText()
            }
        }
    } else {
        ""
    }
}

fun extractJsonFromString(content: String): String {
    var firstIndex = 0
    var lastIndex = 0
    for (i in 0..content.length) {
        if (content[i] == '{') {
            firstIndex = i
            break
        }
    }
    for (j in content.length - 1 downTo 0) {
        if (content[j] == '}') {
            lastIndex = j + 1
            break
        }
    }
    return content.substring(firstIndex, lastIndex)
}


fun getResponse(question: String, callback: (String) -> Unit) {

    val apiKey = "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"
    val url = "https://api.openai.com/v1/chat/completions"
    val prompt =
        "Please analyze and provide a sample summary for the following terms and conditions: $question Then, identify the sections with severity ratings and present the findings in the following format : json ( summary , sections( id(start from 0) ,title ,content,risk(low or middle or high)))"
    val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                  {
                    "role": "user",
                    "content": "$prompt"
                  }
                ],
                "temperature": 1,
                "max_tokens": 500,
                "top_p": 1,
                "frequency_penalty": 0,
                "presence_penalty": 0
            }
        """.trimIndent()

    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("error", "API failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (body != null) {
                Log.v("data", body)
            } else {
                Log.v("data", "empty")
            }
            val jsonObject = body?.let { JSONObject(it) }
            val choicesArray = jsonObject!!.getJSONArray("choices")
            if (choicesArray.length() > 0) {
                val choiceObject = choicesArray.getJSONObject(0)
                val messageObject = choiceObject.getJSONObject("message")
                val content = messageObject.getString("content")
                callback(content.toString())
            } else {
                println("No choices found")
            }
        }
    }
    )
}

fun saveBitmapToFile(bitmap: Bitmap, outputFile: File) {
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}

fun convertPdfToImageURIs(context: Context, inputPdfUri: Uri): List<Uri> {
    val imageURIs = mutableListOf<Uri>()
    val outputDir = context.cacheDir
    val pdfRenderer = PdfRenderer(context.contentResolver.openFileDescriptor(inputPdfUri, "r")!!)
    for (pageIndex in 0 until pdfRenderer.pageCount) {
        val page = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val outputFile = File(outputDir, "page${System.currentTimeMillis()}$pageIndex.jpg")
        saveBitmapToFile(bitmap, outputFile)
        imageURIs.add(outputFile.toUri())
        page.close()
    }
    pdfRenderer.close()
    return imageURIs
}

