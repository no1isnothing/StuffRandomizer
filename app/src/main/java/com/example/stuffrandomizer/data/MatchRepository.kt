package com.example.stuffrandomizer.data

import androidx.lifecycle.LiveData

/**
 * Repo for accessing saved matches (and likely other data)
 * Since there's currently only one data source, this doesn't really do much.
 * It will be more useful if there's some kind of network data also coming in.
 */
class MatchRepository(private val matchSetDao: MatchSetDao) {

    suspend fun getAllMatchSets(): List<MatchSet> {
        return matchSetDao.getAllMatchSets()
    }

    suspend fun insertMatchSet(matchSet: MatchSet) {
        matchSetDao.insert(matchSet)
    }
}