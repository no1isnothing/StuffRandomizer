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
    fun provideMatchDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context.applicationContext,
        MainDatabase::class.java,
        "match_database"
    ).build()

    @Singleton
    @Provides
    fun provideMatchSetDao(database: MainDatabase) = database.pairingGroupDao()

    @Singleton
    @Provides
    fun provideItemListDao(database: MainDatabase) = database.itemListDao()
}