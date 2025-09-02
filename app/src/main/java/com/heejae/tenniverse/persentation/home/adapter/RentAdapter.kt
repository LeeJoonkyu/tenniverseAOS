package com.heejae.tenniverse.persentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.heejae.tenniverse.databinding.ItemPlayTennisBinding
import com.heejae.tenniverse.domain.model.RentModel

class RentAdapter(
    val context: Context,
    val glide: RequestManager,
    val onClick: (RentModel) -> Unit
) :
    PagingDataAdapter<RentModel, RentAdapter.BroadModelViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BroadModelViewHolder(
        ItemPlayTennisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: BroadModelViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class BroadModelViewHolder(private val binding: ItemPlayTennisBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RentModel) {
            binding.item = item
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }


    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<RentModel>() {
            override fun areItemsTheSame(
                oldItem: RentModel,
                newItem: RentModel
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: RentModel,
                newItem: RentModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}