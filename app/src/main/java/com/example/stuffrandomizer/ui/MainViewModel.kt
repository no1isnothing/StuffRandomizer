package com.example.stuffrandomizer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stuffrandomizer.data.MatchRepository
import com.example.stuffrandomizer.data.MatchSet
import com.google.common.flogger.FluentLogger
import kotlinx.coroutines.launch

class MainViewModel(private val matchRepository: MatchRepository) : ViewModel() {
    val logger: FluentLogger = FluentLogger.forEnclosingClass() //move to companion class?
    private val _previewString = MutableLiveData<String?>()

    val previewString: LiveData<String?>
        get() = _previewString

    private val _matches = MutableLiveData<List<MatchSet>>()

    val matches: LiveData<List<MatchSet>>
        get() = _matches

    fun insertMatchSet(matchSet: MatchSet) {
        viewModelScope.launch {
            matchRepository.insertMatchSet(matchSet)
        }
    }

    // also maybe add a temporary button or something to add/remove data from the db instead of doing it in the main activity
    // then start adding tests at least for the data levels as you add to them
    fun getPreviewData() {
        viewModelScope.launch {
            val matches = matchRepository.getAllMatchSets()
            logger.atWarning().log("Matches size %d first match name %s", matches.size, matches[0].matchName)

            _previewString.value = matches[0].matchName
            _matches.value = matches
        }
    }

    companion object {
        /**
         * Factory for creating [MainViewModel]
         *
         * @param arg the repository to pass to [MainViewModel]
         */
        class MainViewModelFactory(private val repository: MatchRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}