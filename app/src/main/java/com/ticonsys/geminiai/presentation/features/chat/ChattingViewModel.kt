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
import com.ticonsys.geminiai.R
import com.ticonsys.geminiai.di.annotation.Pro
import com.ticonsys.geminiai.di.annotation.Vision
import com.ticonsys.geminiai.domain.models.Chat
import com.ticonsys.geminiai.domain.utils.BitmapExtractor
import com.ticonsys.geminiai.domain.utils.SettingsProvider
import com.ticonsys.geminiai.domain.utils.UiText
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
    val settingsProvider: SettingsProvider,
    private val bitmapExtractor: BitmapExtractor,
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
                val images = event.imageUris.map {
                    bitmapExtractor.compressBitmap(it)
                }
                if (event.imageUris.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(images = images)
                    }
                }
            }

            is ChatEvent.OnRemoveImage -> {
                val images = uiState.value.images.toMutableList()
                images.removeAt(event.index)
                _uiState.update { state ->
                    state.copy(images = images)
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

            is ChatEvent.OnShowPreview -> {
                _uiState.update { state ->
                    state.copy(previewBitmap = event.bitmap)
                }
            }

            is ChatEvent.OnCompleteAnimation -> {
                val chats = uiState.value.chats.toMutableList()
                chats.removeLast()
                chats.add(event.answer)
                _uiState.update { state ->
                    state.copy(chats = chats)
                }
            }

            ChatEvent.OnDismissPreview -> {
                _uiState.update { state ->
                    state.copy(previewBitmap = null)
                }
            }


            ChatEvent.Submit -> {
                submitQuestion()
            }
        }
    }

    private fun submitQuestion() {
        if (uiState.value.question.isEmpty()) {
            _uiState.update { state ->
                state.copy(
                    error = UiText.StringResource(id = R.string.type_message)
                )
            }
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
                images = emptyList(),
                error = null,
            )
        }


        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                            message = response.text ?: "",
                            isAnimationComplete = false,
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
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = UiText.DynamicString(e.message ?: "Unknown error occurred")
                    )
                }
            }
        }

    }


    private fun speak(message: String) {
        if (settingsProvider.isSpeaking()) {
            val regex = Regex("[*!#]")
            val clearedMessage = message.replace(regex, "")
            tts?.speak(
                clearedMessage,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "${System.currentTimeMillis()}"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

}