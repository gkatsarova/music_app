package com.example.music_app.data

import androidx.room.TypeConverter

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
