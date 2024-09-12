package com.thebipolaroptimist.stuffrandomizer

import android.content.Context
import androidx.room.Room
import com.thebipolaroptimist.stuffrandomizer.data.MatchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StuffRandomizerAppModule {

    @Singleton
    @Provides
    fun provideMatchDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context.applicationContext,
        MatchDatabase::class.java,
        "match_database"
    ).build()

    @Singleton
    @Provides
    fun provideMatchSetDao(database: MatchDatabase) = database.matchSetDao()

    @Singleton
    @Provides
    fun provideItemListDao(database: MatchDatabase) = database.itemListDao()
}