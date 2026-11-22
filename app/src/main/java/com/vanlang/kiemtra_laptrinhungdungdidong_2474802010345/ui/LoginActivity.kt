package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ActivityLoginBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString()
            )
        }

        viewModel.loginState.observe(this) { state ->
            val color = if (state.isSuccess) 0xFF2E7D32.toInt() else 0xFFD32F2F.toInt()
            binding.tvStatus.setTextColor(color)
            binding.tvStatus.text = state.message
        }
    }
}