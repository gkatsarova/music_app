package com.example.music_app.data

import android.content.Context
import androidx.room.Room

object DatabaseClient {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val tempInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MyAppDB"
            ).build()
            instance = tempInstance
            tempInstance
        }
    }
}