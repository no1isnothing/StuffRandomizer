package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A dao for interacting with [Category]s stored in [MainDatabase].
 */
@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category WHERE name in (:names)")
    suspend fun getCategoriesByName(names: List<String>) : List<Category>

    @Query("SELECT * FROM category WHERE name is (:name)")
    suspend fun getCategoryByName(name: String) : Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<Category>)

    @Query("DELETE FROM category")
    suspend fun deleteAll()
}