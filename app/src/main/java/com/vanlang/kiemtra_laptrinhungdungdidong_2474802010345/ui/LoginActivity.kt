package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ActivityLoginBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.LoginViewModel

/** Màn hình Đăng nhập / Đăng ký / Quên mật khẩu (Firebase Authentication). */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Đã đăng nhập từ trước -> vào thẳng trang chính
        if (viewModel.isAlreadyLoggedIn()) {
            openMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val headerTop = binding.headerBrand.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.headerBrand.setPadding(0, headerTop + bars.top, 0, binding.headerBrand.paddingBottom)
            insets
        }

        binding.toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) setMode(register = checkedId == binding.btnTabRegister.id)
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            if (isRegisterMode) viewModel.register(email, pass) else viewModel.login(email, pass)
        }
        binding.tvForgot.setOnClickListener {
            viewModel.resetPassword(binding.edtEmail.text.toString())
        }

        viewModel.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !loading
            binding.btnSubmit.text = if (loading) "" else submitLabel()
        }

        viewModel.loginState.observe(this) { state ->
            binding.tvStatus.visibility = View.VISIBLE
            binding.tvStatus.setTextColor(if (state.isSuccess) 0xFF2E7D32.toInt() else 0xFFD32F2F.toInt())
            binding.tvStatus.text = state.message
        }

        viewModel.loggedIn.observe(this) { ok -> if (ok) openMain() }
    }

    private fun setMode(register: Boolean) {
        isRegisterMode = register
        binding.tvStatus.visibility = View.GONE
        binding.tvFormTitle.text = if (register) "Tạo tài khoản mới" else "Chào mừng trở lại"
        binding.tvFormSubtitle.text =
            if (register) "Chỉ mất vài giây để bắt đầu mua sắm"
            else "Đăng nhập để xem giỏ hàng và sản phẩm yêu thích"
        binding.tvForgot.visibility = if (register) View.GONE else View.VISIBLE
        binding.btnSubmit.text = submitLabel()
    }

    private fun submitLabel() = if (isRegisterMode) "Tạo tài khoản" else "Đăng nhập"

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
