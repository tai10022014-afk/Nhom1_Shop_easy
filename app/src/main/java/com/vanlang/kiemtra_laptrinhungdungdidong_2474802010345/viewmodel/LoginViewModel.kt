package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.LoginState
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.AuthRepository

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(rawEmail: String, password: String) {
        val email = rawEmail.trim()

        _loginState.value = when {
            email.isEmpty() && password.isEmpty() ->
                LoginState("Lỗi: Vui lòng nhập Email và Mật khẩu!", false)
            email.isEmpty() ->
                LoginState("Lỗi: Email không được để trống!", false)
            password.isEmpty() ->
                LoginState("Lỗi: Mật khẩu không được để trống!", false)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                LoginState("Lỗi: Email sai định dạng!", false)
            password.length < 6 ->
                LoginState("Lỗi: Mật khẩu phải có ít nhất 6 ký tự!", false)
            repository.authenticate(email, password) ->
                LoginState("Xác thực thành công!", true)
            else ->
                LoginState("Lỗi: Đăng nhập thất bại!", false)
        }
    }
}