package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * A dao for interacting with [MatchSet]s stored in [MatchDatabase].
 */
@Dao
interface MatchSetDao {
    //TODO #4: Determine order for MatchSet retrieval.
    @Query("SELECT * FROM matchset")
    suspend fun getAllMatchSets(): List<MatchSet>

    //TODO #1: decide on conflict strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(matchSet: MatchSet)

    @Query("DELETE FROM matchset")
    suspend fun deleteAll()
}