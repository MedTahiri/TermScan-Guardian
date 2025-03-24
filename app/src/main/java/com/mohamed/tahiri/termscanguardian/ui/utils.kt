package com.mohamed.tahiri.termscanguardian.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.ai.client.generativeai.GenerativeModel
import com.mohamed.tahiri.termscanguardian.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

//fun saveNameToFile(context: Context, name: String) {
//    val fileName = "fullName.txt"
//    val file = File(context.filesDir, fileName)
//    FileOutputStream(file).use {
//        it.write(name.toByteArray())
//    }
//}
//
//fun getNameFromFile(context: Context): String {
//    val fileName = "fullName.txt"
//    val file = File(context.filesDir, fileName)
//    return if (file.exists()) {
//        FileInputStream(file).use { inputStream ->
//            InputStreamReader(inputStream).use { reader ->
//                reader.readText()
//            }
//        }
//    } else {
//        ""
//    }
//}

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


suspend fun getResponse(question: String, callback: (String) -> Unit) {

    val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with most use cases
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.API_KEY
    )


    val prompt =
        "Analyze the following terms and conditions and provide a concise summary that avoids repetition and minimizes word count and use same language for: $question Then, identify the sections with severity ratings and present the findings in the following format : json ( summary , sections( id(start from 0) ,title ,content,risk(low or middle or high)))"

    val response = generativeModel.generateContent(prompt)
    Log.v("important", response.text.toString())
    callback(response.text.toString())

    //    val apiKey = BuildConfig.API_KEY
//    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

//    val json = JSONObject().apply {
//        put("contents", listOf(
//            JSONObject().apply {
//                put("parts", listOf(JSONObject().apply { put("text", prompt) }))
//            }
//        ))
//    }
//
//    val requestBody = RequestBody.create(
//        "application/json".toMediaTypeOrNull(), json.toString()
//    )
//
//    val client = OkHttpClient()
//    val request = Request.Builder()
//        .url(url)
//        .post(requestBody)
//        .addHeader("Content-Type", "application/json")
//        .build()

//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            println("Request failed: ${e.message}")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            response.use {
//                if (!it.isSuccessful) {
//                    println("Request failed: ${it.code()}")
//                } else {
//                    println("Response: ${it.body()?.string()}")
//                }
//            }
//        }
//    })

//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            Log.e("error", "API failed", e)
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            val body = response.body?.string()
//            if (body != null) {
//                Log.v("data", body)
//            } else {
//                Log.v("data", "empty")
//            }
//            val jsonObject = body?.let { JSONObject(it) }
//            val choicesArray = jsonObject!!.getJSONArray("choices")
//            if (choicesArray.length() > 0) {
//                val choiceObject = choicesArray.getJSONObject(0)
//                val messageObject = choiceObject.getJSONObject("message")
//                val content = messageObject.getString("content")
//                callback(content.toString())
//            } else {
//                println("No choices found")
//            }
//        }
//    }
//  )

}

fun fetchResponse(question: String, callback: (String) -> Unit) {
    // Launch a coroutine on the IO dispatcher (for network operations)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Call the suspend function
            getResponse(question, callback)
        } catch (e: Exception) {
            // Handle any errors
            Log.e("Error", "Failed to get response: ${e.message}")
        }
    }
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

