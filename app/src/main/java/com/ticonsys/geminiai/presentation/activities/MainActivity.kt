package com.ticonsys.geminiai.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ticonsys.geminiai.presentation.features.chat.NavGraphs
import com.ticonsys.geminiai.presentation.features.chat.destinations.ChattingScreenDestination
import com.ticonsys.geminiai.presentation.features.components.TypewriterText
import com.ticonsys.geminiai.presentation.theme.GeminiAITheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiAITheme {
                val navController = rememberNavController()

                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController,
                    startRoute = ChattingScreenDestination
                )
            }
        }
    }
}
