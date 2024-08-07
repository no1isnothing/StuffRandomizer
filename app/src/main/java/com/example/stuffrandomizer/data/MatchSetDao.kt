package com.example.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MatchSetDao {
    @Query("SELECT * FROM matchset") //TODO: add order here?
    fun getAllMatchSets(): List<MatchSet>

    @Insert(onConflict = OnConflictStrategy.IGNORE) //TODO: decide on conflict strategy
    suspend fun insert(matchSet: MatchSet)

    @Query("DELETE FROM matchset")
    suspend fun deleteAll()

    //TODO: Add delete, query, and update by id
}