package com.ticonsys.geminiai.presentation.features.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onRemove: (() -> Unit)? = null,
) {

    Box(modifier = modifier) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        if (onRemove != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .clickable {
                        onRemove()
                    }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }
    }

}