package com.ticonsys.geminiai.presentation.features.settings

import androidx.lifecycle.ViewModel
import com.ticonsys.geminiai.domain.utils.SettingsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsProvider: SettingsProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            isSpeaking = settingsProvider.isSpeaking(),
            imageSize = settingsProvider.getImageSize()
        )
    )
    val uiState: StateFlow<SettingUiState>
        get() = _uiState.asStateFlow()

    fun onEvent(event: SettingEvent) {
        when (event) {
            is SettingEvent.OnImageSizeChanged -> {
                settingsProvider.updateImageSize(event.imageSize)
                _uiState.update { state ->
                    state.copy(imageSize = event.imageSize)
                }
            }

            is SettingEvent.OnSpeakingChanged -> {
                settingsProvider.updateSpeaking(event.isSpeaking)
                _uiState.update { state ->
                    state.copy(isSpeaking = event.isSpeaking)
                }
            }
        }
    }

}