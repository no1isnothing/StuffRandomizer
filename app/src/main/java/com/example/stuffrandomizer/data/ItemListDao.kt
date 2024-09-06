package com.example.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * A dao for interacting with [ItemList]s stored in [MatchDatabase].
 */
@Dao
interface ItemListDao {
    @Query("SELECT * FROM itemlist")
    suspend fun getAllItemLists(): List<ItemList>

    //TODO #1: decide on conflict strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(itemList: ItemList)

    @Query("DELETE FROM itemlist")
    suspend fun deleteAll()
}