package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Lưu phiên đăng nhập, mã hóa bằng EncryptedSharedPreferences (AES-256).
 *
 * Khởi tạo EncryptedSharedPreferences/MasterKey (Android Keystore) rất chậm (hàng trăm ms),
 * nên chỉ tạo MỘT lần cho cả tiến trình và được "làm nóng" trên luồng nền ngay khi app mở
 * (xem [warmUp], gọi từ ShopEasyApp) thay vì tạo lại trên luồng chính ở mỗi màn hình.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = securePrefs(context.applicationContext)

    fun save(uid: String, email: String) {
        prefs.edit().putString(KEY_UID, uid).putString(KEY_EMAIL, email).apply()
    }

    fun isLoggedIn(): Boolean = !prefs.getString(KEY_UID, null).isNullOrEmpty()
    fun email(): String? = prefs.getString(KEY_EMAIL, null)
    fun uid(): String? = prefs.getString(KEY_UID, null)
    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_UID = "uid"
        private const val KEY_EMAIL = "email"

        @Volatile
        private var cached: SharedPreferences? = null

        private fun securePrefs(appContext: Context): SharedPreferences =
            cached ?: synchronized(this) {
                cached ?: create(appContext).also { cached = it }
            }

        private fun create(context: Context): SharedPreferences = try {
            val key = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "session_secure",
                key,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Keystore lỗi trên một số máy/emulator: dùng prefs thường (chỉ chứa uid + email)
            context.getSharedPreferences("session_fallback", Context.MODE_PRIVATE)
        }

        /** Tạo sẵn prefs mã hóa ở luồng nền để lúc cần dùng không bị khựng UI. */
        fun warmUp(context: Context) {
            val app = context.applicationContext
            Thread({ securePrefs(app) }, "session-warmup").start()
        }
    }
}
