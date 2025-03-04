package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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
    suspend fun getCategoryByName(name: String) : Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<Category>)

    @Query("DELETE FROM category")
    suspend fun deleteAll()

    @Query("SELECT * FROM category WHERE uid is (:uid)")
    suspend fun getById(uid: UUID) : Category

    @Query("SELECT * FROM category WHERE uid in (:uids)")
    suspend fun getByIds(uids: List<UUID>) : List<Category>
}