package com.example.music_app.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.music_app.data.AppDatabase
import com.example.music_app.viewmodel.UserViewModel

class UserViewModelFactory(
    private val application: Application,
    private val db: AppDatabase,
    private val currentUserId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application, db, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}