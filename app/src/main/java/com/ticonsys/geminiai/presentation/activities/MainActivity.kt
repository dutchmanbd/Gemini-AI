package com.ticonsys.geminiai.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ticonsys.geminiai.presentation.features.NavGraphs
import com.ticonsys.geminiai.presentation.features.destinations.ChattingScreenDestination
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
