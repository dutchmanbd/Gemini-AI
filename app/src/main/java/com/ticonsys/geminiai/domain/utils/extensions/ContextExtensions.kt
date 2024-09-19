package com.ticonsys.geminiai.domain.utils.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileInputStream

fun Context.getFile(contentUri: Uri): File {
    val fileName = contentResolver.getFileName(contentUri).replace(" ", "")
    val file = File(cacheDir, fileName)
    file.createNewFile()
    file.outputStream().use { outputStream ->
        contentResolver.openInputStream(contentUri)?.use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file
}

fun File.toBitmap(): Bitmap {
    return FileInputStream(this).use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}