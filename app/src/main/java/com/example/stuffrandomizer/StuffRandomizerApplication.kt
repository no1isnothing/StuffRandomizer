package com.example.stuffrandomizer

import android.app.Application
import com.example.stuffrandomizer.data.MatchDatabase
import com.example.stuffrandomizer.data.MatchRepository

class StuffRandomizerApplication : Application() {
    val database by lazy { MatchDatabase.getDatabase(this) }
    val repository by lazy { MatchRepository(database.matchSetDao(),
        database.itemListDao()) }
}