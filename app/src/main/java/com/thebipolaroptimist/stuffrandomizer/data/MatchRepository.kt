package com.thebipolaroptimist.stuffrandomizer.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for accessing saved [MatchSet]s and [ItemList]s
 * Notes:
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if/when there's some kind of network data also coming in.
 * Similarly using Flow over LiveData here would make more sense if there's multiple data sources.
 */
class MatchRepository @Inject constructor(private val matchSetDao: MatchSetDao,
                                          private val itemListDao: ItemListDao) {

    fun getAllMatchSets(): Flow<List<MatchSet>> {
        return matchSetDao.getAllMatchSets()
    }

    fun getAllItemLists(): Flow<List<ItemList>> {
        return itemListDao.getAllItemLists()
    }

    suspend fun insertMatchSet(matchSet: MatchSet) {
        matchSetDao.insert(matchSet)
    }

    suspend fun insertItemList(itemList: ItemList) {
        itemListDao.insert(itemList)
    }

    suspend fun deleteAllMatchSets() {
        matchSetDao.deleteAll()
    }

    suspend fun deleteAllItemLists() {
        itemListDao.deleteAll()
    }
}