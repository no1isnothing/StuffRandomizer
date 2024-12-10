package com.thebipolaroptimist.stuffrandomizer

import android.content.Context
import androidx.room.Room
import com.thebipolaroptimist.stuffrandomizer.data.MainDatabase
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
    fun provideMainDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context.applicationContext,
        MainDatabase::class.java,
        "main_database"
    ).build()

    @Singleton
    @Provides
    fun providePartyDao(database: MainDatabase) = database.partyDao()

    @Singleton
    @Provides
    fun provideCategoryDao(database: MainDatabase) = database.categoryDao()
}