package com.example.music_app.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.viewmodel.TrackViewModel

class TrackViewModelFactory(private val repository: MusicRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}