package com.thebipolaroptimist.stuffrandomizer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface Repository {
    fun getAllParties(): Flow<List<Party>>

    fun getAllCategories(): Flow<List<Category>>

    suspend fun getCategoriesByName(names : List<String>): List<Category>

    suspend fun getCategoryByName(name: String): Category?

    suspend fun insertParty(party: Party)

    suspend fun insertCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun insertCategories(categories: List<Category>)

    suspend fun deleteAllParties()
    suspend fun deleteAllCategories()
}
/*
 * Repository for accessing saved [Party]s and [Category]s
 * Notes:
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if/when there's some kind of network data also coming in.
 * Similarly using Flow over LiveData here would make more sense if there's multiple data sources.
 */
class MainRepository @Inject constructor(private val partyDao: PartyDao,
                                         private val categoryDao: CategoryDao) : Repository {
    override fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllParties()
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoriesByName(names : List<String>): List<Category> {
        return categoryDao.getCategoriesByName(names)
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)
    }

    override suspend fun insertParty(party: Party) {
        partyDao.insertOrUpdate(party)
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insert(categories)
    }

    override suspend fun deleteAllParties() {
        partyDao.deleteAll()
    }

    override suspend fun deleteAllCategories() {
        categoryDao.deleteAll()
    }
}