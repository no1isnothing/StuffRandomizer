package com.example.stuffrandomizer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MatchSet::class], version = 1)
@TypeConverters(MatchConverter::class)
abstract class MatchDatabase : RoomDatabase()  {
    abstract fun matchSetDao(): MatchSetDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MatchDatabase? = null

        // do we need this or is there a way to inject the database
        fun getDatabase(context: Context): MatchDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MatchDatabase::class.java,
                    "match_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}