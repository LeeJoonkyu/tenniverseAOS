package com.heejae.tenniverse.persentation.home.user

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.ActivityMyPageBinding
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.adapter.MarginItemDecoration
import com.heejae.tenniverse.persentation.home.adapter.MyRentAdapter
import com.heejae.tenniverse.persentation.home.rentdetail.RentDetailActivity
import com.heejae.tenniverse.persentation.home.user.profile.ProfileActivity
import com.heejae.tenniverse.persentation.splash.SplashActivity
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_MODEL
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(R.layout.activity_my_page) {

    private val viewModel: MyPageViewModel by viewModels()

    private lateinit var reservationAdapter: MyRentAdapter

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.notLoadingInitData()
        }
    }

    override fun setViewModel() = viewModel

    override fun initView() {
        initBinding()
        setListener()
        setViewModelObserver()
        initAdapter()
    }

    private fun setViewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

        reservationAdapter = MyRentAdapter(this, glide) {
            gotoRentDetail(it)
        }

        binding.rvRent.adapter = reservationAdapter
        binding.rvRent.addItemDecoration(
            MarginItemDecoration(verticalSize = resources.getDimensionPixelSize(R.dimen.list_margin))
        )
    }

    private fun gotoRentDetail(model: RentModel) {
        launcher.launch(
            Intent(this, RentDetailActivity::class.java).apply {
                putExtra(PUT_EXTRA_RENT_MODEL, model)
            }
        )
    }

    fun initBinding() {
        binding.viewModel = viewModel
        binding.glide = Glide.with(this)
    }

    fun setListener() {
        binding.clProfileContainer.setOnClickListener {
            launcher.launch(Intent(this, ProfileActivity::class.java).apply {
                putExtra(PUT_EXTRA_USER, viewModel.initUserModel)
            })
        }
        binding.llAlarm.setOnClickListener {
            alarmCheckPermission()
        }
        binding.llLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            goToSplash()
        }
        binding.llDeleteUser.setOnClickListener {
            showDialog(DialogType.DELETE_ACCOUNT) {
                goToSplash()
            }
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
    }

    private fun goToSplash() {
        startActivity(
            Intent(this, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private fun alarmCheckPermission() {
        startActivity(
            Intent().apply {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, applicationContext.packageName)
                    }

                    else -> {
                        action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        putExtra("app_package", applicationContext.packageName)
                        putExtra("app_uid", applicationContext.applicationInfo.uid)
                    }
                }
            }
        )
    }
}