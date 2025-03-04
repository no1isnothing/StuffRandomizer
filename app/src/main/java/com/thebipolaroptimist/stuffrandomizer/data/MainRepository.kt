package com.thebipolaroptimist.stuffrandomizer.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for accessing saved [Party]s and [Category]s
 * Notes:
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if/when there's some kind of network data also coming in.
 * Similarly using Flow over LiveData here would make more sense if there's multiple data sources.
 */
class MainRepository @Inject constructor(private val partyDao: PartyDao,
                                         private val categoryDao: CategoryDao) {

    fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllParties()
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getCategoriesByName(names : List<String>): List<Category> {
        return categoryDao.getCategoriesByName(names)
    }

    suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)
    }

    suspend fun insertParty(party: Party) {
        partyDao.insertOrUpdate(party)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insert(categories)
    }

    suspend fun deleteAllParties() {
        partyDao.deleteAll()
    }

    suspend fun deleteAllCategories() {
        categoryDao.deleteAll()
    }
}