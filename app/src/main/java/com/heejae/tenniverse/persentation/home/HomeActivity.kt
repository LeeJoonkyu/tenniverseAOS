package com.heejae.tenniverse.persentation.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentHomeBinding
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.domain.model.UserType
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.adapter.RentAdapter
import com.heejae.tenniverse.persentation.home.adapter.ReservationAdapter
import com.heejae.tenniverse.persentation.home.management.UserManagementActivity
import com.heejae.tenniverse.persentation.home.rent.day.RentActivity
import com.heejae.tenniverse.persentation.home.rent.event.EventRentActivity
import com.heejae.tenniverse.persentation.home.rent.regular.RegularRentActivity
import com.heejae.tenniverse.persentation.home.rentdetail.RentDetailActivity
import com.heejae.tenniverse.persentation.home.user.MyPageActivity
import com.heejae.tenniverse.util.PUT_EXTRA_IS_WEEKLY
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_MODEL
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_UID
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var rentAdapter: RentAdapter
    private lateinit var reservationAdapter: ReservationAdapter

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.refresh()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun initView() {
        initBinding()
        initAdapter()
        setViewModelObserver()
        setListener()
        askNotificationPermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.refresh()
    }

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.rentList.collectLatest {
                        rentAdapter.submitData(it)
                    }
                }
                launch {
                    viewModel.reservations.collectLatest {
                        reservationAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        val glide = Glide.with(this)

        rentAdapter = RentAdapter(this, glide) {
            viewModel.checkRent(it.uid) {
                gotoRentDetail(it)
            }
        }
        reservationAdapter = ReservationAdapter(this, glide) {
            gotoRentDetail(it)
        }

        binding.rvRent.adapter = rentAdapter
        binding.rvRent.addItemDecoration(
            MarginItemDecoration(verticalSize = resources.getDimensionPixelSize(R.dimen.list_margin))
        )
        binding.rvReserve.adapter = reservationAdapter
        binding.rvReserve.addItemDecoration(
            MarginItemDecoration(horizontalSize = resources.getDimensionPixelSize(R.dimen.list_margin))
        )
    }

    private fun gotoRentDetail(model: RentModel) {
        launcher.launch(
            Intent(this, RentDetailActivity::class.java).apply {
                putExtra(PUT_EXTRA_RENT_MODEL, model)
                putExtra(PUT_EXTRA_RENT_UID, model.uid)
            }
        )
    }

    override fun setViewModel() = viewModel

    private fun setListener() {
        binding.fab.setOnClickListener {
            when (viewModel.selectedRent.value) {
                RentType.ALL -> goRent()
                RentType.WEEKLY -> {
                    if (viewModel.user.value?.checkRegularUser() == true) {
                        goRegularRent()
                    }else {
                        goRent()
                    }
                }
                RentType.EVENT -> goEvent()
                RentType.DAILY -> goRent()
            }
        }
        binding.ivUserManagement.setOnClickListener {
            launcher.launch(
                Intent(this, UserManagementActivity::class.java)
            )
        }

        binding.tlTennis.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                DEBUG(this@HomeActivity.name, "id: ${tab?.position}")
                tab?.let {
                    viewModel.setSelectedRent(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.ivUser.setOnClickListener {
            launcher.launch(
                Intent(this, MyPageActivity::class.java).apply {
                    putExtra(PUT_EXTRA_USER, viewModel.user.value)
                }
            )
        }

        binding.swipe.setOnRefreshListener {
            viewModel.refresh {
                binding.swipe.isRefreshing = false
            }
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun goRent() {
        launcher.launch(
            Intent(
                this,
                RentActivity::class.java
            ).apply {
                putExtra(PUT_EXTRA_USER, viewModel.user.value)
            }
        )
    }

    private fun goEvent() {
        launcher.launch(
            Intent(
                this,
                EventRentActivity::class.java
            ).apply {
                putExtra(PUT_EXTRA_USER, viewModel.user.value)
            }
        )
    }

    private fun goRegularRent() {
        startActivity(
            Intent(
                this,
                RegularRentActivity::class.java
            ).apply {
                putExtra(PUT_EXTRA_USER, viewModel.user.value)
                putExtra(PUT_EXTRA_IS_WEEKLY, true)
            }
        )
    }
}