package com.mohamed.tahiri.termscanguardian.ui

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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