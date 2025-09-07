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
    fun loadAllData(onComplete: (message: String) -> Unit) {
        viewModelScope.launch {
            repository.loadAllData { success, message ->
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
