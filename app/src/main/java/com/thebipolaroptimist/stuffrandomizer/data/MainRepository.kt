package com.thebipolaroptimist.stuffrandomizer.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for accessing saved [Party]s and [Stuff]
 * Notes:
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if/when there's some kind of network data also coming in.
 * Similarly using Flow over LiveData here would make more sense if there's multiple data sources.
 */
class MainRepository @Inject constructor(private val partyDao: PartyDao,
                                         private val stuffDao: StuffDao) {

    fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllParties()
    }

    fun getAllStuff(): Flow<List<Stuff>> {
        return stuffDao.getAllStuff()
    }

    suspend fun insertParty(party: Party) {
        partyDao.insert(party)
    }

    suspend fun insertStuff(stuff: Stuff) {
        stuffDao.insert(stuff)
    }

    suspend fun deleteAllParties() {
        partyDao.deleteAll()
    }

    suspend fun deleteAllStuff() {
        stuffDao.deleteAll()
    }
}