package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 *  A composable for displaying when a screen has an empty state
 */
@Preview
@Composable
fun EmptyStateScreen(
    tagline: String = "This is empty",
    message: String = "Add something to make it not empty"
) {
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(
            tagline,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}