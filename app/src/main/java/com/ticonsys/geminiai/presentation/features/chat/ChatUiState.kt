package com.ticonsys.geminiai.presentation.features.chat

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import com.ticonsys.geminiai.domain.models.Chat


@Stable
data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val question: String = "",
    val images: List<Bitmap> = emptyList(),
    val isSpeaking: Boolean = false,
    val isLoading: Boolean = false,
)
