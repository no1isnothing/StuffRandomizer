package com.thebipolaroptimist.stuffrandomizer

import android.app.Application
import com.thebipolaroptimist.stuffrandomizer.data.MatchDatabase
import com.thebipolaroptimist.stuffrandomizer.data.MatchRepository

class StuffRandomizerApplication : Application() {
    val database by lazy { MatchDatabase.getDatabase(this) }
    val repository by lazy { MatchRepository(database.matchSetDao(),
        database.itemListDao()) }
}