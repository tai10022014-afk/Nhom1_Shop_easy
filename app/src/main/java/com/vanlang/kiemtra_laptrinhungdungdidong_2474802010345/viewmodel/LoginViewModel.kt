package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.LoginState
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

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
            val result = repository.loginWithFirebase(email, password)
            _loginState.value = result.fold(
                onSuccess = {
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
            val result = repository.registerWithFirebase(email, password)
            _loginState.value = result.fold(
                onSuccess = {
                    LoginState(message = "Đăng ký thành công!", isSuccess = true)
                },
                onFailure = { e ->
                    LoginState(message = "Đăng ký thất bại: ${e.localizedMessage}", isSuccess = false)
                }
            )
        }
    }
}