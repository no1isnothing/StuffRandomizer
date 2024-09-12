package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.data.MatchRepository
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for interactions between the [MainActivity] and it's Fragment
 * and the [MatchRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val matchRepository: MatchRepository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    private val _previewString = MutableLiveData<String?>()

    val previewString: LiveData<String?>
        get() = _previewString

    private val _matches = MutableLiveData<List<MatchSet>>()

    val matches: LiveData<List<MatchSet>>
        get() = _matches

    private val _itemLists = MutableLiveData<List<ItemList>>()

    val itemLists: LiveData<List<ItemList>>
        get() = _itemLists

    fun insertMatchSet(matchSet: MatchSet) {
        viewModelScope.launch {
            matchRepository.insertMatchSet(matchSet)
        }
    }

    fun insertItemList(itemList: ItemList) {
        viewModelScope.launch {
            matchRepository.insertItemList(itemList)
        }
    }

    fun getPreviewData() {
        viewModelScope.launch {
            // check on this loading correctly after new info is added
            val matches = matchRepository.getAllMatchSets()
            val itemLists = matchRepository.getAllItemLists()

            if(matches.isNotEmpty()) {
                logger.atWarning().log("Matches size %d first match name %s", matches.size, matches[0].matchName)
                _previewString.value = matches[0].matchName
            }
            _matches.value = matches
            _itemLists.value = itemLists
        }
    }
}