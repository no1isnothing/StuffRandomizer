package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.thebipolaroptimist.stuffrandomizer.StuffRandomizerApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * The launching [AppCompatActivity] for the project. Starts the [HomeScreen].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { StuffRandomizerApp() }
    }
}