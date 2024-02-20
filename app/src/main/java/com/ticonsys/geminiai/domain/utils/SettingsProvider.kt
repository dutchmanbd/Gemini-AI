package com.ticonsys.geminiai.domain.utils

import android.content.Context

class SettingsProvider(
    context: Context
) {

    private val sharedPref =
        SharedPref(context, storageName = "${context.packageName}.settings_provider")

    companion object {
        private const val PREF_IS_SPEAKING = "pref_is_speaking"
        private const val PREF_IMAGE_SIZE = "pref_image_size"
    }

    fun updateSpeaking(isSpeaking: Boolean) {
        sharedPref.write(PREF_IS_SPEAKING, isSpeaking)
    }

    fun isSpeaking(): Boolean {
        return sharedPref.read(PREF_IS_SPEAKING, false)
    }

    fun updateImageSize(imageSize: ImageSize) {
        sharedPref.write(PREF_IMAGE_SIZE, imageSize.size)
    }

    fun getImageSize(): ImageSize {
        val size = sharedPref.read(PREF_IMAGE_SIZE, ImageSize.DEFAULT_SIZE.size)
        return ImageSize.imageSize(size)
    }


}