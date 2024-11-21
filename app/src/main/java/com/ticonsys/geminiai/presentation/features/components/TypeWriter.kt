package com.ticonsys.geminiai.presentation.features.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.colintheshots.twain.MarkdownText
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    message: String,
    textStyle: TextStyle = TextStyle(),
    isAnimated: Boolean = true,
    onCompleteAnimation: () -> Unit = {}
) {
    var currentCharacterIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(message, isAnimated) {
        if (isAnimated) {
            message.forEachIndexed { index, _ ->
                delay(65) // Delay between characters
                currentCharacterIndex = index + 1
                if (currentCharacterIndex >= message.length) {
                    onCompleteAnimation()
                }
            }
        }
    }

    MarkdownText(
        markdown = message.take(currentCharacterIndex),
        modifier = Modifier.fillMaxWidth(),
        style = textStyle,
    )

//    Text(
//        text = message.take(currentCharacterIndex),
//        modifier = Modifier.fillMaxWidth(),
//        fontFamily = FontFamily.Monospace,
//        style = textStyle,
//    )
}