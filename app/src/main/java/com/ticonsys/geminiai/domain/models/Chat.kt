package com.ticonsys.geminiai.domain.models

import android.graphics.Bitmap


sealed class Chat {

    data class Question(
        val question: String,
        val images: List<Bitmap>,
    ) : Chat()

    data class Answer(
        val message: String
    ) : Chat()


}