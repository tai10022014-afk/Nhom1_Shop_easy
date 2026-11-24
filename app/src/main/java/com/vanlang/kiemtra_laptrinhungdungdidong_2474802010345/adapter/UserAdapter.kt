package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ItemUserBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserDto

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var items: List<UserDto> = emptyList()

    fun submitList(newList: List<UserDto>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = items[position]
        holder.binding.tvName.text = "${user.firstName} ${user.lastName}"
        holder.binding.tvEmail.text = user.email
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)
}