package com.thebipolaroptimist.stuffrandomizer

import android.content.Context
import androidx.room.Room
import com.thebipolaroptimist.stuffrandomizer.data.MainDatabase
import com.thebipolaroptimist.stuffrandomizer.data.MainRepository
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
interface StuffRandomizerBindingModule {
    @Binds
    fun bindMainRepository(
        mainRepository: MainRepository
    ): Repository
}

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