package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.data.SessionManager
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.LoginState
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val session = SessionManager(application)

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /** true khi đăng nhập/đăng ký thành công -> chuyển sang màn hình chính */
    private val _loggedIn = MutableLiveData(false)
    val loggedIn: LiveData<Boolean> = _loggedIn

    /** Firebase tự lưu phiên đăng nhập, nên chỉ cần hỏi Repository (không đọc prefs mã hóa lúc khởi động). */
    fun isAlreadyLoggedIn(): Boolean = repository.currentUser() != null

    private fun validateInput(email: String, password: String): String? {
        return when {
            email.isEmpty() && password.isEmpty() -> "Lỗi: Vui lòng nhập Email và Mật khẩu!"
            email.isEmpty() -> "Lỗi: Email không được để trống!"
            password.isEmpty() -> "Lỗi: Mật khẩu không được để trống!"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Lỗi: Email sai định dạng!"
            password.length < 6 -> "Lỗi: Mật khẩu phải có ít nhất 6 ký tự!"
            else -> null
        }
    }

    fun login(rawEmail: String, password: String) {
        val email = rawEmail.trim()
        val errorMsg = validateInput(email, password)
        if (errorMsg != null) {
            _loginState.value = LoginState(message = errorMsg, isSuccess = false)
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repository.loginWithFirebase(email, password)
            _loading.value = false
            _loginState.value = result.fold(
                onSuccess = { user ->
                    session.save(user.uid, user.email ?: email)
                    _loggedIn.value = true
                    LoginState(message = "Đăng nhập thành công!", isSuccess = true)
                },
                onFailure = { e ->
                    LoginState(message = "Đăng nhập thất bại: ${e.localizedMessage}", isSuccess = false)
                }
            )
        }
    }

    fun register(rawEmail: String, password: String) {
        val email = rawEmail.trim()
        val errorMsg = validateInput(email, password)
        if (errorMsg != null) {
            _loginState.value = LoginState(message = errorMsg, isSuccess = false)
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repository.registerWithFirebase(email, password)
            _loading.value = false
            _loginState.value = result.fold(
                onSuccess = { user ->
                    session.save(user.uid, user.email ?: email)
                    _loggedIn.value = true
                    LoginState(message = "Đăng ký thành công!", isSuccess = true)
                },
                onFailure = { e ->
                    LoginState(message = "Đăng ký thất bại: ${e.localizedMessage}", isSuccess = false)
                }
            )
        }
    }

    fun resetPassword(rawEmail: String) {
        val email = rawEmail.trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = LoginState("Lỗi: Nhập email hợp lệ để đặt lại mật khẩu!", false)
            return
        }
        viewModelScope.launch {
            _loading.value = true
            val result = repository.sendPasswordReset(email)
            _loading.value = false
            _loginState.value = result.fold(
                onSuccess = { LoginState("Đã gửi email đặt lại mật khẩu tới $email", true) },
                onFailure = { e -> LoginState("Gửi email thất bại: ${e.localizedMessage}", false) }
            )
        }
    }
}
