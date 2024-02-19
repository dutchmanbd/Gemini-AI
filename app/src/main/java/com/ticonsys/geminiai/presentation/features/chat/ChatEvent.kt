package com.ticonsys.geminiai.presentation.features.chat

import android.graphics.Bitmap

sealed interface ChatEvent {

    data class OnQuestionChanged(
        val question: String
    ) : ChatEvent

    data class OnImageSelected(
        val images: List<Bitmap>
    ) : ChatEvent

//    data class Speak(
//        val message: String
//    ) : ChatEvent

    data object Submit : ChatEvent

}