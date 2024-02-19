package com.ticonsys.geminiai.presentation.features.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(message: String, textStyle: TextStyle = TextStyle()) {
    var currentCharacterIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(message) {
        message.forEachIndexed { index, _ ->
            delay(65) // Delay between characters
            currentCharacterIndex = index + 1
        }
    }

    Text(
        text = message.take(currentCharacterIndex),
        modifier = Modifier.fillMaxWidth(),
        style = textStyle,
    )
}