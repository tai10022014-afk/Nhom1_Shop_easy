package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter.UserAdapter
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ActivityLoginBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Resource
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.LoginViewModel
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.UserListViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val userViewModel: UserListViewModel by viewModels()
    private val userAdapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = userAdapter

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString()
            )
        }

        binding.btnLoadUsers.setOnClickListener {
            userViewModel.loadUsers()
        }

        viewModel.loginState.observe(this) { state ->
            val color = if (state.isSuccess) 0xFF2E7D32.toInt() else 0xFFD32F2F.toInt()
            binding.tvStatus.setTextColor(color)
            binding.tvStatus.text = state.message
        }

        userViewModel.users.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvUsers.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.rvUsers.visibility = View.VISIBLE
                    userAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvUsers.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = resource.message
                }
            }
        }
    }
}