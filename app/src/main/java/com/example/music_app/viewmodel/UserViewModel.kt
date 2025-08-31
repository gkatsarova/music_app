package com.example.music_app.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.AppDatabase
import com.example.music_app.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PREFS_NAME = "user_session_prefs"
private const val KEY_USER_ID = "logged_in_user_id"

class UserViewModel(application: Application, private val db: AppDatabase, private val currentUserId: Int): ViewModel() {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()
    private val _logoutState = MutableStateFlow<Boolean?>(null)
    val logoutState: StateFlow<Boolean?> = _logoutState.asStateFlow()
    private val _deleteProfileState = MutableStateFlow<Boolean?>(null)
    val deleteProfileState: StateFlow<Boolean?> = _deleteProfileState.asStateFlow()

    init {
        loadUserProfile(currentUserId)
    }

    private fun loadUserProfile(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = db.userDao().getUserById(userId)
            _user.value = user
        }
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    fun logoutUser() {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                remove(KEY_USER_ID)
                apply()
            }
            _user.value = null
            _logoutState.value = true
        }
    }

    fun resetLogoutState() {
            _logoutState.value = null

    }

}
