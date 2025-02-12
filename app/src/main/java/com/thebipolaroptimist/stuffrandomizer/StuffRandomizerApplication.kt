package com.thebipolaroptimist.stuffrandomizer

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StuffRandomizerApplication : Application()

@Composable
fun StuffRandomizerApp() {
    val navController = rememberNavController()
    StuffRandomizerNavHost(navController = navController)
}