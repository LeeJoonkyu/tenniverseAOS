package com.heejae.tenniverse.persentation.home.adapter

import android.content.Context
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.heejae.tenniverse.databinding.ItemUserManagementBinding
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.domain.model.UserType
import com.heejae.tenniverse.util.extension.getUserType

class ManagementAdapter(
    val context: Context,
    val glide: RequestManager,
    val onRemoved: (UserModel) -> Unit,
    val onChangeUserType: (UserModel) -> Unit,
) :
    PagingDataAdapter<UserModel, ManagementAdapter.BroadModelViewHolder>(broadDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BroadModelViewHolder(
        ItemUserManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: BroadModelViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    override fun onBindViewHolder(
        holder: BroadModelViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.bind(getItem(position) ?: return)
        }
        else {
            holder.update(payloads[0] as Bundle)
        }
    }

    inner class BroadModelViewHolder(private val binding: ItemUserManagementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserModel) {
            DEBUG(this@ManagementAdapter.name, "type: ${item.type}")
            binding.model = item
            binding.glide = glide
            binding.userType = context.getString(item.type.kor)

            val listPopupAdapter = ListPopupAdapter(context, binding.anchorView, items) {
                onChangeUserType(item.copy(type = items[it].getUserType(context)))
            }

            binding.tvUserRate.setOnClickListener {
                listPopupAdapter.listPopupWindow.show()
            }
            binding.tvName.setOnClickListener {
                onRemoved(item)
            }
        }

        fun update(bundle: Bundle) {
            val type = bundle.getParcelable<UserType>(DIFF_USER_TYPE) ?: return
            DEBUG(this@ManagementAdapter.name, "update type: $type")
            binding.userType = context.getString(type.kor)
        }
    }

    companion object {

        const val DIFF_USER_TYPE = "DIFF_USER_TYPE"
        val items = listOf("클럽장", "코트장", "정회원", "준회원")

        val broadDiffUtil = object : DiffUtil.ItemCallback<UserModel>() {
            override fun areItemsTheSame(
                oldItem: UserModel,
                newItem: UserModel
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: UserModel,
                newItem: UserModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: UserModel, newItem: UserModel): Any? {
                if (oldItem.uid == newItem.uid) {
                    if (oldItem.type == newItem.type) {
                        return super.getChangePayload(oldItem, newItem)
                    }
                    bundleOf(
                        DIFF_USER_TYPE to newItem.type
                    )
                }
                return super.getChangePayload(oldItem, newItem)
            }
        }
    }
}