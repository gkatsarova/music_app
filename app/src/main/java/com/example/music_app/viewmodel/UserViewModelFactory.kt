package com.example.music_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.music_app.data.AppDatabase

class UserViewModelFactory(
    private val application: Application,
    private val db: AppDatabase,
    private val currentUserId: Int
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application, db, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}