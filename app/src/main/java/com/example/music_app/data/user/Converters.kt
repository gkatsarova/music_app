package com.example.music_app.data.user

import androidx.room.TypeConverter
import com.example.music_app.data.user.UserRole

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromUserRole(role: UserRole?): String? {
            return role?.name
        }

        @TypeConverter
        @JvmStatic
        fun toUserRole(roleName: String?): UserRole? {
            if (roleName == null) {
                return null
            }
            return try {
                UserRole.valueOf(roleName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}