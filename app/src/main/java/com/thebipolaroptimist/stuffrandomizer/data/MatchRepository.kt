package com.thebipolaroptimist.stuffrandomizer.data

/**
 * Repository for accessing saved [MatchSet]s and [ItemList]s
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if/when there's some kind of network data also coming in.
 */
class MatchRepository(private val matchSetDao: MatchSetDao,
    private val itemListDao: ItemListDao) {

    suspend fun getAllMatchSets(): List<MatchSet> {
        return matchSetDao.getAllMatchSets()
    }

    suspend fun getAllItemLists(): List<ItemList> {
        return itemListDao.getAllItemLists()
    }

    suspend fun insertMatchSet(matchSet: MatchSet) {
        matchSetDao.insert(matchSet)
    }

    suspend fun insertItemList(itemList: ItemList) {
        itemListDao.insert(itemList)
    }
}