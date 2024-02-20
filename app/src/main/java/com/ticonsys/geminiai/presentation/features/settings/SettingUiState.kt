package com.ticonsys.geminiai.presentation.features.settings

import com.ticonsys.geminiai.domain.utils.ImageSize

data class SettingUiState(
    val isSpeaking: Boolean = false,
    val imageSize: ImageSize = ImageSize.DEFAULT_SIZE
)