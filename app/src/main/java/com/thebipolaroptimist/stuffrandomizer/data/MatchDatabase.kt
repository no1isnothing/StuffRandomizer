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

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MatchDatabase? = null

        //TODO #2: Investigate dependency injection
        fun getDatabase(context: Context): MatchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MatchDatabase::class.java,
                    "match_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}