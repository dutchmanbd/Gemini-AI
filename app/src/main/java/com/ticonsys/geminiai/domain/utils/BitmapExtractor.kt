package com.ticonsys.geminiai.domain.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import com.ticonsys.geminiai.domain.utils.extensions.getFile
import com.ticonsys.geminiai.domain.utils.extensions.toBitmap
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.math.sqrt


class BitmapExtractor(
    private val context: Context,
    private val settingsProvider: SettingsProvider,
) {

    fun compressBitmap(contentUri: Uri): Bitmap {
        Log.e("BitmapExtractor", "compressBitmap: ${contentUri.path}")
        context.contentResolver.takePersistableUriPermission(contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val imageFile = context.getFile(contentUri)
        Log.e("BitmapExtractor", "compressBitmap: ${imageFile.absolutePath}")
        val orientation = try {
            val exif = ExifInterface(imageFile.absolutePath)
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val decodeBitmap = imageFile.toBitmap()
        val bitmap = decodeBitmap.resizeImage(
            orientation = orientation
        )

        return try {
            val maxSizeBytes =
                settingsProvider.getImageSize().size * 1024 // Adjust based on your desired limit
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height

            // Choose the most efficient combination of resizing and quality reduction
            val compressedData = tryResizeFirst(bitmap, maxSizeBytes, originalWidth, originalHeight)
                ?: tryQualityCompression(bitmap, maxSizeBytes)

            if (compressedData != null) {
                byteArrayToBitmap(compressedData) ?: bitmap
            } else {
                bitmap
            }
        } catch (e: Exception) {
            bitmap
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun tryResizeFirst(
        bitmap: Bitmap,
        maxSizeBytes: Int,
        originalWidth: Int,
        originalHeight: Int
    ): ByteArray? {
        // Calculate a scaling factor to fit within the size limit, preserving aspect ratio
        val scalingFactor = min(
            sqrt(maxSizeBytes / ((originalWidth * originalHeight) * 4.0)), // Factor of 4 for ARGB_8888
            1.0
        )

        if (scalingFactor < 1.0f) {
            // Resize if necessary
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (originalWidth * scalingFactor).toInt(),
                (originalHeight * scalingFactor).toInt(),
                true
            )

            // Compress the resized bitmap
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                outputStream
            ) // Start with good quality

            val compressedData = outputStream.toByteArray()
            if (compressedData.size <= maxSizeBytes) {
                return compressedData
            }
        }

        return null // Resizing wasn't enough or not possible
    }

    private fun tryQualityCompression(
        bitmap: Bitmap,
        maxSizeBytes: Int,
    ): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        var quality = 80 // Start with moderate quality

        while (outputStream.size() > maxSizeBytes) {
            outputStream.reset() // Clear previous write
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // Reduce quality gradually
        }

        return outputStream.toByteArray()
    }


    private fun Bitmap.resizeImage(
        newWidth: Int = 150,
        newHeight: Int = 150,
        orientation: Int = ExifInterface.ORIENTATION_NORMAL
    ): Bitmap {
        val originalWidth = width
        val originalHeight = height

        // Maintain aspect ratio
        val scaleX = newWidth.toFloat() / originalWidth.toFloat()
        val scaleY = newHeight.toFloat() / originalHeight.toFloat()
        val scale = min(scaleX, scaleY)

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL, ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(
                180f
            )

            ExifInterface.ORIENTATION_TRANSPOSE, ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(
                90f
            )

            ExifInterface.ORIENTATION_TRANSVERSE -> matrix.setRotate(-90f)
        }
        return Bitmap.createBitmap(this, 0, 0, originalWidth, originalHeight, matrix, true)
    }

}