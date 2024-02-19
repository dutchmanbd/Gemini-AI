package com.ticonsys.geminiai.presentation.features.chat

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.ticonsys.geminiai.di.annotation.Pro
import com.ticonsys.geminiai.di.annotation.Vision
import com.ticonsys.geminiai.domain.models.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    application: Application,
    @Pro private val geminiPro: GenerativeModel,
    @Vision private val geminiProVision: GenerativeModel,
) : AndroidViewModel(application) {

    private var tts: TextToSpeech? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState>
        get() = _uiState.asStateFlow()

    init {
        tts = TextToSpeech(application.applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _uiState.update { state ->
                                state.copy(
                                    isSpeaking = true
                                )
                            }
                        }

                        override fun onDone(utteranceId: String?) {
                            _uiState.update { state ->
                                state.copy(
                                    isSpeaking = false
                                )
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            _uiState.update { state ->
                                state.copy(
                                    isSpeaking = false
                                )
                            }
                        }

                    }
                )

//                speak(
//                    """
//                    Welcome to Genimi AI.
//                    Write your question in TextField.
//                """.trimIndent()
//                )
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OnImageSelected -> {
                _uiState.update { state ->
                    state.copy(images = event.images)
                }
            }

            is ChatEvent.OnQuestionChanged -> {
                _uiState.update { state ->
                    state.copy(
                        question = event.question
                    )
                }
            }

//            is ChatEvent.Speak -> {
//                speak(event.message)
//            }

            ChatEvent.Submit -> {
                submitQuestion()
            }
        }
    }

    private fun submitQuestion() {
        if (uiState.value.question.isEmpty()) {
            return
        }

        val isImageExists = uiState.value.images.isNotEmpty()

        val inputContent = content {
            uiState.value.images.forEachIndexed { _, bitmap ->
                image(bitmap)
            }
            text(uiState.value.question)
        }

        _uiState.update { state ->
            val chats = state.chats.toMutableList()
            chats.add(
                Chat.Question(
                    question = state.question,
                    images = state.images,
                )
            )

            state.copy(
                chats = chats,
                isLoading = true,
                question = "",
                images = emptyList()
            )
        }


        viewModelScope.launch(Dispatchers.IO) {
            val response =
                if (!isImageExists)
                    geminiPro.generateContent(inputContent)
                else
                    geminiProVision.generateContent(
                        inputContent
                    )


            val message = response.text ?: ""
            if (message.isNotEmpty()) {
                val chats = uiState.value.chats.toMutableList()
                chats.add(
                    Chat.Answer(
                        response.text ?: ""
                    )
                )
                _uiState.update { state ->
                    state.copy(
                        chats = chats,
                        isLoading = false,
                    )
                }
                speak(message)
            }
        }

    }


    private fun speak(message: String) {
        val regex = Regex("[*!#]")
        val clearedMessage = message.replace(regex, "")
        tts?.speak(
            clearedMessage,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "${System.currentTimeMillis()}"
        )
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

}