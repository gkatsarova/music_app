package com.example.music_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.data.repository.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {
    private val _searchResult = MutableStateFlow(SearchResult(emptyList(), emptyList(), emptyList()))
    val searchResult: StateFlow<SearchResult> = _searchResult

    private var isDataLoaded = false
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadAllData(onComplete: (message: String) -> Unit) {
        if (isDataLoaded) {
            onComplete("")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            repository.loadAllData { success, message ->
                _loading.value = false
                if (success) isDataLoaded = true
                onComplete(message)
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val result = repository.searchAll(query)
            _searchResult.value = result
        }
    }

}
