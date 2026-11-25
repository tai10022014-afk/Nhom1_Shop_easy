package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ItemUserBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity

class UserAdapter : ListAdapter<UserEntity, UserAdapter.UserViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserEntity) {
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.imgAvatar.load(user.image) {
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<UserEntity>() {
            override fun areItemsTheSame(a: UserEntity, b: UserEntity) = a.uid == b.uid
            override fun areContentsTheSame(a: UserEntity, b: UserEntity) = a == b
        }
    }
}