package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun loginWithFirebase(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
                ?: return Result.failure(Exception("Không lấy được thông tin tài khoản"))
            saveProfile(user, email.trim())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithFirebase(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
                ?: return Result.failure(Exception("Không tạo được tài khoản"))
            saveProfile(user, email.trim())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveProfile(user: FirebaseUser, email: String) {
        try {
            val name = email.substringBefore("@")
            val profile = mapOf(
                "uid" to user.uid,
                "email" to email,
                "name" to name,
                "photoUrl" to "https://ui-avatars.com/api/?name=${Uri.encode(name)}&size=128&background=random"
            )
            firestore.collection("users").document(user.uid)
                .set(profile, SetOptions.merge())
                .await()
        } catch (_: Exception) {
        }
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
}