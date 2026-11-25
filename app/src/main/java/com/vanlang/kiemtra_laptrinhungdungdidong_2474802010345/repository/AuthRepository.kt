package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.UserDao
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val userDao: UserDao,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val users: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun loginWithFirebase(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return Result.failure(Exception("Không lấy được thông tin tài khoản"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithFirebase(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return Result.failure(Exception("Không tạo được tài khoản"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun signOut() {
        auth.signOut()
    }

    // Giữ lại nếu chỗ khác đang gọi
    suspend fun authenticate(email: String, password: String): Boolean {
        return userDao.findUser(email, password) != null
    }

    suspend fun seedDefaultUser() {
        userDao.insert(UserEntity(email = "a@gmail.com", password = "123456"))
    }
}