package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users ORDER BY name COLLATE NOCASE")
    fun getAllUsers(): Flow<List<UserEntity>>
}