package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A dao for interacting with [ItemList]s stored in [MatchDatabase].
 */
@Dao
interface ItemListDao {
    @Query("SELECT * FROM itemlist")
    fun getAllItemLists(): Flow<List<ItemList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemList: ItemList)

    @Query("DELETE FROM itemlist")
    suspend fun deleteAll()
}