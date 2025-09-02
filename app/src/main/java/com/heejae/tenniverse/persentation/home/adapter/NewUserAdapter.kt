package com.heejae.tenniverse.persentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.heejae.tenniverse.databinding.ItemNewUserBinding
import com.heejae.tenniverse.domain.model.UserModel

class NewUserAdapter(
    val glide: RequestManager,
    val onClick: (List<UserModel>) -> Unit
) : PagingDataAdapter<UserModel, NewUserAdapter.ViewHolder>(diffUtil) {

    val selected = mutableListOf<UserModel>()

    inner class ViewHolder(private val binding: ItemNewUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: UserModel) {
            binding.model = item
            binding.glide = glide
            binding.selected = selected.any { it == item }

            binding.root.setOnClickListener {
                binding.selected = if(selected.remove(item)) {
                    false
                }else {
                    selected.add(item)
                    true
                }
                onClick(selected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemNewUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position) ?: return)
    }

    fun clearSelected() {
        selected.clear()
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<UserModel>() {

            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: UserModel,
                newItem: UserModel,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}