package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * A Room database for storing [Party]s and the [Category]s to create them.
 */
@Database(entities = [Party::class, Category::class], version = 1)
@TypeConverters(MainConverter::class)
abstract class MainDatabase : RoomDatabase()  {
    abstract fun partyDao(): PartyDao
    abstract fun categoryDao(): CategoryDao
}