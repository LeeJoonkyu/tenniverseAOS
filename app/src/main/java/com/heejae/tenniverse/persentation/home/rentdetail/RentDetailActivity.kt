package com.heejae.tenniverse.persentation.home.rentdetail

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentRentDetailBinding
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.domain.model.RentUserType
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.adapter.RentDetailAdapter
import com.heejae.tenniverse.persentation.home.rentdetail.comment.RentDetailCommentActivity
import com.heejae.tenniverse.util.PUT_EXTRA_FROM_FCM_RENT_UID
import com.heejae.tenniverse.util.PUT_EXTRA_RENT
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_MEMBER_COUNT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RentDetailActivity : BaseActivity<FragmentRentDetailBinding>(R.layout.fragment_rent_detail) {
    private val viewModel: RentDetailViewModel by viewModels()
    private lateinit var memberAdapter: RentDetailAdapter
    private lateinit var waitingAdapter: RentDetailAdapter


    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.initData()
        }
    }

    override fun initView() {
        initBinding()
        initAdapter()
        setViewModelObserver()
        setListener()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val rentUid = intent?.getStringExtra(PUT_EXTRA_FROM_FCM_RENT_UID) ?: return
        viewModel.initData(rentUid)
    }

    override fun setViewModel() = viewModel

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.members.collectLatest {
                        memberAdapter.submitList(it)
                    }
                }
                launch {
                    viewModel.waiting.collectLatest {
                        waitingAdapter.submitList(it)
                    }
                }
                launch {
                    viewModel.isAskWantToParticipate.collectLatest {
                        if (it.isAsk) {
                            showDialog(DialogType.ASK_PARTICIPATE) {
                                viewModel.waitingToMember(it.des)
                            }
                            viewModel.initAskWantToParticipate()
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        val glide = Glide.with(this)
        memberAdapter =
            RentDetailAdapter(this, glide, viewModel.userType.value == RentUserType.ROOT) {
                showDialog(DialogType.DELETE_RENT_USER) {
                    viewModel.removeParticipatedUser(true, it)
                }
            }
        waitingAdapter =
            RentDetailAdapter(this, glide, viewModel.userType.value == RentUserType.ROOT) {
                showDialog(DialogType.DELETE_RENT_USER) {
                    viewModel.removeParticipatedUser(false, it)
                }
            }
        binding.rvMemberList.apply {
            adapter = memberAdapter
            addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))
        }
        binding.rvWaitList.apply {
            adapter = waitingAdapter
            addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))
        }
    }

    private fun setListener() {
        binding.btnPart.setOnClickListener {
            val user = viewModel.getUser()

            if (user != null) {
                // 참가 취소
                showDialog(DialogType.CANCEL_PARTICIPATE) {
                    viewModel.removeParticipatedUser(
                        viewModel.rentModel.value.isMember(user.uid),
                        user
                    )
                }
            } else {
                // 참가
                viewModel.recruit(viewModel.rentModel.value.des)
            }
        }
        binding.btnReject.setOnClickListener {
            if (!viewModel.rentModel.value.closed) {
                showDialog(DialogType.DEADLINE_RECRUIT) {
                    viewModel.recruit()
                }
            } else {
                viewModel.recruit()
            }
        }
        binding.btnStandard.setOnClickListener {
            val user = viewModel.getUser()
            // 참가 취소
            if (user != null) {
                val uid = user.uid
                showDialog(DialogType.CANCEL_PARTICIPATE) {
                    viewModel.removeParticipatedUser(viewModel.rentModel.value.isMember(uid), user)
                }
            } else {
                // 2주 전이고 모집 마감 되지 않았을 때만 참가 가능.
                if (viewModel.checkIsBeforeTwoWeek() && !viewModel.rentModel.value.closed) {
                    gotoRentDetailComment()
                }
            }
        }
        binding.ivDelete.setOnClickListener {
            showDialog(DialogType.DELETE_RENT) {
                viewModel.deleteRent {
                    setResult()
                }
            }
        }
        binding.swipe.setOnRefreshListener {
            viewModel.initData {
                binding.swipe.isRefreshing = false
            }
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
        binding.tvCopy.setOnClickListener {
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(
                    ClipData.newPlainText(null, viewModel.rentModel.value.ownerAccount)
                )
            }
        }
    }

    private fun gotoRentDetailComment() {
        launcher.launch(
            Intent(this, RentDetailCommentActivity::class.java).apply {
                putExtra(PUT_EXTRA_RENT, viewModel.rentModel.value)
                putExtra(
                    PUT_EXTRA_RENT_MEMBER_COUNT,
                    viewModel.members.value.size + viewModel.waiting.value.size
                )
            }
        )
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    override fun handleBackPressed() {
        if (viewModel.rentIdFromFCM == null) {
            animationFinished()
        } else {
            goToHome()
        }
    }
}