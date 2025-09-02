package com.heejae.tenniverse.persentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.heejae.tenniverse.databinding.ItemChoiceUserBinding
import com.heejae.tenniverse.domain.model.UserModel

class RentUserChoiceAdapter(
    val glide: RequestManager,
    val onClick: (List<UserModel>) -> Unit
) : PagingDataAdapter<UserModel, RentUserChoiceAdapter.ViewHolder>(diffUtil) {

    val selected = mutableListOf<UserModel>()

    inner class ViewHolder(private val binding: ItemChoiceUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: UserModel) {
            binding.model = item
            binding.glide = glide
            binding.selected = selected.any { it == item }
            binding.isRoot = if(selected.isNotEmpty()) selected[0] == item else false

            binding.root.setOnClickListener {
                binding.selected = if(selected.remove(item)) {
                    false
                }else {
                    selected.add(item)
                    true
                }
                val flag = binding.isRoot
                binding.isRoot = if(selected.isNotEmpty()) selected[0] == item else false
                if (flag == true && binding.isRoot == false) {
                    updateSelected()
                }
                onClick(selected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemChoiceUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position) ?: return)
    }

    fun updateSelected() {
        notifyItemRangeChanged(0, itemCount)
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