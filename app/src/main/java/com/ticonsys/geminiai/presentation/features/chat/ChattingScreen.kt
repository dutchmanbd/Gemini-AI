package com.ticonsys.geminiai.presentation.features.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ticonsys.geminiai.domain.models.Chat
import com.ticonsys.geminiai.presentation.features.components.ImageItem
import com.ticonsys.geminiai.presentation.features.components.TypewriterText
import com.ticonsys.geminiai.presentation.features.destinations.SettingScreenDestination
import kotlinx.coroutines.delay

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@RootNavGraph(start = true)
@Destination
@Composable
fun ChattingScreen(
    navigator: DestinationsNavigator
) {

    val viewModel = hiltViewModel<ChattingViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current

    val chatListState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(key1 = state.chats) {
        delay(100)
        if (state.chats.isNotEmpty()) {
            chatListState.animateScrollToItem(state.chats.size)
        }
    }

    val pickFileLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(
                5
            )
        ) { uris ->
            viewModel.onEvent(ChatEvent.OnImageSelected(uris))
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Genimi AI")
                },
                actions = {
                    IconButton(onClick = {
                        navigator.navigate(
                            SettingScreenDestination
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 68.dp,
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp
                    ),
                state = chatListState
            ) {


                items(
                    state.chats.size,
                    key = { index ->
                        (index + 1)
                    }
                ) { index ->
                    when (val chat = state.chats[index]) {
                        is Chat.Answer -> {
                            if (chat.message.isNotEmpty()) {
                                TypewriterText(
                                    message = chat.message,
                                    isAnimated = !chat.isAnimationComplete,
                                    onCompleteAnimation = {
                                        viewModel.onEvent(
                                            ChatEvent.OnCompleteAnimation(
                                                chat.copy(isAnimationComplete = true)
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        is Chat.Question -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()

                            ) {

                                FlowRow {
                                    chat.images.forEachIndexed { index, bitmap ->
                                        ImageItem(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Image: $index",
                                            modifier = Modifier
                                                .height(50.dp)
                                                .width(50.dp)
                                                .clickable {
                                                    viewModel.onEvent(
                                                        ChatEvent.OnShowPreview(bitmap)
                                                    )
                                                }
                                        )
                                    }
                                }

                                Text(
                                    text = "Q: ${chat.question}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W600
                                )
                            }

                        }
                    }
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    )
                    .align(alignment = Alignment.BottomCenter),
            ) {
                if (state.error != null) {
                    state.error?.let { text ->
                        Text(
                            text = text.asString(context),
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                FlowRow {
                    state.images.forEachIndexed { index, bitmap ->
                        ImageItem(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Image: $index",
                            modifier = Modifier
                                .height(90.dp)
                                .width(90.dp)
                                .clickable {
                                    viewModel.onEvent(
                                        ChatEvent.OnShowPreview(bitmap)
                                    )
                                },
                            onRemove = {
                                viewModel.onEvent(ChatEvent.OnRemoveImage(index))
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    IconButton(
                        onClick = {
                            pickFileLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        enabled = !state.isLoading && !state.isSpeaking
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attachment"
                        )
                    }

                    TextField(
                        value = state.question,
                        onValueChange = { question ->
                            viewModel.onEvent(
                                ChatEvent.OnQuestionChanged(question)
                            )
                        },
                        placeholder = {
                            Text(text = "Write your question")
                        },
                        maxLines = 1,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .weight(1f)
                    )

                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.onEvent(
                                ChatEvent.Submit
                            )
                        },
                        enabled = !state.isLoading && !state.isSpeaking
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }


        }
    }

    if (state.previewBitmap != null) {
        Dialog(
            onDismissRequest = {
                viewModel.onEvent(ChatEvent.OnDismissPreview)
            },
            properties = DialogProperties(

            )
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = state.previewBitmap!!.asImageBitmap(),
                    contentDescription = "Bitmap",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(150.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.background
                        )
                        .clickable {
                            viewModel.onEvent(ChatEvent.OnDismissPreview)
                        }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }


            }
        }
    }
}