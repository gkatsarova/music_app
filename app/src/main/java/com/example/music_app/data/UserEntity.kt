package com.example.music_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "user",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "role") val role: UserRole = UserRole.LISTENER,
    @ColumnInfo(name = "profile_picture") val profilePicture: String? = null
)