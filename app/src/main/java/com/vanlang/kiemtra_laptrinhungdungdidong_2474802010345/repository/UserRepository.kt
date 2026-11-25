package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import androidx.room.withTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.google.firebase.firestore.Source
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.AppDatabase
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Resource
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val dao = db.userDao()

    val users: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun refresh(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users").get(Source.SERVER).await()
            val entities = snapshot.documents.map { doc ->
                UserEntity(
                    uid = doc.id,
                    name = doc.getString("name").orEmpty(),
                    email = doc.getString("email").orEmpty(),
                    image = doc.getString("photoUrl")
                )
            }
            db.withTransaction {
                dao.clear()
                dao.insertAll(entities)
            }
            Resource.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Resource.Error(
                when (e.code) {
                    Code.INVALID_ARGUMENT -> "Lỗi 400: Yêu cầu không hợp lệ"
                    Code.UNAUTHENTICATED,
                    Code.PERMISSION_DENIED -> "Lỗi 401: Chưa đăng nhập hoặc không có quyền. Hãy đăng nhập rồi tải lại."
                    Code.UNAVAILABLE,
                    Code.DEADLINE_EXCEEDED -> "Không có kết nối mạng. Đang hiển thị dữ liệu đã lưu trong máy."
                    Code.INTERNAL,
                    Code.UNKNOWN -> "Lỗi 500: Lỗi máy chủ"
                    else -> "Lỗi Firestore: ${e.code}"
                }
            )
        } catch (e: Exception) {
            Resource.Error("Đã xảy ra lỗi: ${e.message}")
        }
    }
}