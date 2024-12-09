package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A dao for interacting with [Stuff]s stored in [MainDatabase].
 */
@Dao
interface StuffDao {
    @Query("SELECT * FROM stuff")
    fun getAllItemLists(): Flow<List<Stuff>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stuff: Stuff)

    @Query("DELETE FROM stuff")
    suspend fun deleteAll()
}