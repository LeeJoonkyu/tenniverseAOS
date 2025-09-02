package com.heejae.tenniverse.persentation.home.adapter

import android.content.Context
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.heejae.tenniverse.databinding.ItemRentUserBinding
import com.heejae.tenniverse.domain.model.UserRentModel

class RentDetailAdapter(
    val context: Context,
    val glide: RequestManager,
    val isRoot: Boolean = false,
    val onClick: (UserRentModel) -> Unit
) :
    ListAdapter<UserRentModel, RentDetailAdapter.BroadModelViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BroadModelViewHolder(
        ItemRentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: BroadModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BroadModelViewHolder(private val binding: ItemRentUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserRentModel) {
            binding.model = item
            binding.glide = glide
            binding.isRoot = isRoot

            DEBUG(this@RentDetailAdapter.name, "model: ${item.root}")

            binding.icDeleteUser.setOnClickListener {
                onClick(item)
            }
        }
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<UserRentModel>() {
            override fun areItemsTheSame(
                oldItem: UserRentModel,
                newItem: UserRentModel
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: UserRentModel,
                newItem: UserRentModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}