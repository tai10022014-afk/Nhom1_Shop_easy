package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345

import android.app.Application
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.data.SessionManager

class ShopEasyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Việc nặng (Keystore) chạy nền ngay lúc mở app, không chặn màn hình đầu tiên.
        SessionManager.warmUp(this)
    }
}
