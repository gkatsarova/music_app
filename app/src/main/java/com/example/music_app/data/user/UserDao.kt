package com.example.music_app.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.music_app.data.user.UserEntity

@Dao
interface UserDao{
    @Insert
    suspend fun registerUser(user: UserEntity): Long

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUser(email: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun checkEmailExists(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun checkUsernameExists(username: String): UserEntity?

    @Query("SELECT * FROM user WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: Int): UserEntity?

    @Query("DELETE FROM user WHERE uid = :uid")
    suspend fun deleteUserById(uid: Int): Int

    @Update
    suspend fun updateUser(user: UserEntity)
}