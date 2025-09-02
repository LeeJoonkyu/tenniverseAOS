package com.heejae.tenniverse.persentation.home.newuser

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentNewUserBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.adapter.NewUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewUserActivity :
    BaseActivity<FragmentNewUserBinding>(R.layout.fragment_new_user) {

    private val viewModel: NewUserViewModel by viewModels()
    private lateinit var adapter: NewUserAdapter
    override fun setViewModel() = viewModel

    override fun initView() {
        initBinding()
        initAdapter()
        setViewModelObserver()
        setListener()
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userList.collectLatest {
                    adapter.apply {
                        clearSelected()
                        submitData(it)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = NewUserAdapter(Glide.with(this)) {
            viewModel.setSelectedList(it)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin))
        )
    }

    private fun setListener() {
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
        binding.btnReject.setOnClickListener {
            viewModel.reject {
                setResult()
            }
        }
        binding.btnPart.setOnClickListener {
            viewModel.accept {
                setResult()
            }
        }
    }
}