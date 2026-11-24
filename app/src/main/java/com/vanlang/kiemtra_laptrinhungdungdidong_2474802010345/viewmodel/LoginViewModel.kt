package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.AppDatabase
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.LoginState
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(
        AppDatabase.getInstance(application).userDao()
    )

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    val users: LiveData<List<UserEntity>> = repository.users.asLiveData()

    init {
        viewModelScope.launch {
            repository.seedDefaultUser()
        }
    }

    fun login(rawEmail: String, password: String) {
        val email = rawEmail.trim()

        viewModelScope.launch {
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
                    LoginState("Lỗi: Sai email hoặc mật khẩu!", false)
            }
        }
    }
}