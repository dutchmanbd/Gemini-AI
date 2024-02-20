package com.ticonsys.geminiai.domain.utils.extensions

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File



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

private fun ContentResolver.getContentFileName(uri: Uri): String? = runCatching {
    query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()