package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.UserDao
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val userDao: UserDao) {

    val users: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun authenticate(email: String, password: String): Boolean {
        return userDao.findUser(email, password) != null
    }

    suspend fun seedDefaultUser() {
        userDao.insert(UserEntity(email = "a@gmail.com", password = "123456"))
    }
}