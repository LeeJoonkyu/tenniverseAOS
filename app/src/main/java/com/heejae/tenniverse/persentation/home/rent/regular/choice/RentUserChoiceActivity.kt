package com.heejae.tenniverse.persentation.home.rent.regular.choice

import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentRentUserChoiceBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.adapter.RentUserChoiceAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RentUserChoiceActivity :
    BaseActivity<FragmentRentUserChoiceBinding>(R.layout.fragment_rent_user_choice) {

    private val viewModel: RentUserViewModel by viewModels()
    private lateinit var adapter: RentUserChoiceAdapter
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
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = RentUserChoiceAdapter(Glide.with(this)) {
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
        binding.btnPart.setOnClickListener {
            viewModel.accept {
                goToHome()
            }
        }
    }
}