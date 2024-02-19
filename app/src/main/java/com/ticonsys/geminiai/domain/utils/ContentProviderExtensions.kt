package com.ticonsys.geminiai.domain.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import kotlin.math.min


fun ContentResolver.mimeType(uri: Uri): String {
    return getType(uri) ?: ""
}

fun ContentResolver.getExtensions(uri: Uri): String {
    return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(mimeType(uri)) ?: ""
    } else {
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: "")).toString())
    }
}

fun ContentResolver.getFileName(uri: Uri): String = when (uri.scheme) {
    ContentResolver.SCHEME_CONTENT -> getContentFileName(uri) ?: "${System.currentTimeMillis()}.jpg"
    else -> uri.path?.let(::File)?.name ?: "${System.currentTimeMillis()}${getExtensions(uri)}"
}

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

fun Context.toBitmap(contentUri: Uri): Bitmap {
    contentResolver.takePersistableUriPermission(contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val file = getFile(contentUri)
    return FileInputStream(file).use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.resizeImage()
    }
}

private fun Bitmap.resizeImage(
    newWidth: Int = 150,
    newHeight: Int = 150
): Bitmap {
    val originalWidth = width
    val originalHeight = height

    // Maintain aspect ratio
    val scaleX = newWidth.toFloat() / originalWidth.toFloat()
    val scaleY = newHeight.toFloat() / originalHeight.toFloat()
    val scale = min(scaleX, scaleY)

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    return Bitmap.createBitmap(this, 0, 0, originalWidth, originalHeight, matrix, true)
}


private fun ContentResolver.getContentFileName(uri: Uri): String? = runCatching {
    query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()