package com.ticonsys.geminiai.presentation.features.settings

import com.ticonsys.geminiai.domain.utils.ImageSize

sealed interface SettingEvent {

    data class OnSpeakingChanged(
        val isSpeaking: Boolean
    ) : SettingEvent

    data class OnImageSizeChanged(
        val imageSize: ImageSize
    ) : SettingEvent



}