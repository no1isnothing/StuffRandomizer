package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A dao for interacting with [Party]s stored in [MainDatabase].
 */
@Dao
interface PartyDao {
    //TODO #4: Determine order for Party retrieval.
    @Query("SELECT * FROM party")
    fun getAllParties(): Flow<List<Party>>

    //TODO #1: decide on conflict strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(party: Party)

    @Query("DELETE FROM party")
    suspend fun deleteAll()
}