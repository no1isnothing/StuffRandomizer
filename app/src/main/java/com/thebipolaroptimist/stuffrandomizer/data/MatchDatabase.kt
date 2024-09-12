package com.thebipolaroptimist.stuffrandomizer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * A room database for storing matches and the information to create them.
 */
@Database(entities = [MatchSet::class, ItemList::class], version = 1)
@TypeConverters(MatchConverter::class)
abstract class MatchDatabase : RoomDatabase()  {
    abstract fun matchSetDao(): MatchSetDao
    abstract fun itemListDao(): ItemListDao
}