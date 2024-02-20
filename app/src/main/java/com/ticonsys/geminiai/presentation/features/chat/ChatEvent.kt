package com.ticonsys.geminiai.presentation.features.chat

import android.graphics.Bitmap
import android.net.Uri
import com.ticonsys.geminiai.domain.models.Chat

sealed interface ChatEvent {

    data class OnQuestionChanged(
        val question: String
    ) : ChatEvent

    data class OnImageSelected(
        val imageUris: List<Uri>
    ) : ChatEvent

    data class OnRemoveImage(
        val index: Int
    ) : ChatEvent

    data class OnCompleteAnimation(
        val answer: Chat.Answer
    ) : ChatEvent

    data class OnShowPreview(val bitmap: Bitmap) : ChatEvent

    data object OnDismissPreview : ChatEvent

//    data class Speak(
//        val message: String
//    ) : ChatEvent

    data object Submit : ChatEvent

}