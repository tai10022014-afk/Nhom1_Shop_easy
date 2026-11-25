package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.data.SessionManager
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.AuthRepository

/** Thông tin tài khoản hiển thị ở tab Cá nhân. */
data class AccountInfo(val name: String, val email: String, val uid: String) {
    val avatarUrl: String
        get() = "https://ui-avatars.com/api/?name=${java.net.URLEncoder.encode(name, "UTF-8")}&size=192&background=random"
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = AuthRepository()
    private val session = SessionManager(application)

    /** Tính một lần; Fragment không cần biết dữ liệu đến từ Session hay Firebase. */
    val account: AccountInfo by lazy {
        val user = auth.currentUser()
        val email = session.email() ?: user?.email ?: "khach@shopeasy.app"
        AccountInfo(
            name = email.substringBefore("@"),
            email = email,
            uid = session.uid() ?: user?.uid ?: "-"
        )
    }

    fun logout() {
        auth.signOut()
        session.clear()
    }
}
