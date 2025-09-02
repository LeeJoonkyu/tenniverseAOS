package com.heejae.tenniverse.persentation.home.management

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentUserManagementBinding
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.ManagementAdapter
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.newuser.NewUserActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserManagementActivity :
    BaseActivity<FragmentUserManagementBinding>(R.layout.fragment_user_management) {

    private val viewModel: ManagementViewModel by viewModels()
    private lateinit var pagingAdapter: ManagementAdapter
    override fun setViewModel() = viewModel

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when(result.resultCode) {
            RESULT_OK or RESULT_CANCELED -> {
                viewModel.getUserPaging()
            }
        }
    }

    override fun initView() {
        initBinding()
        setListener()
        setViewModelObserver()
        initAdapter()
    }

    private fun initBinding() {

    }

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userList.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }
    }

    private fun initAdapter() {
        pagingAdapter = ManagementAdapter(
            this,
            Glide.with(this),
            onChangeUserType = {
                viewModel.setUserRate(it)
            },
            onRemoved =  {
                showDialog(
                    DialogType.DELETE_USER
                ) {
                    viewModel.removeUser(it)
                }
            }
        )

        binding.recyclerView.adapter = pagingAdapter
        binding.recyclerView.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin))
        )
    }
    private fun setListener() {
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
        binding.ivNew.setOnClickListener {
            launcher.launch(Intent(this, NewUserActivity::class.java))
        }
    }
}