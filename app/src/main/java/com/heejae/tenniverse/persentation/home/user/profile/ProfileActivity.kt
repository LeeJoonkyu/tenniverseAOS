package com.heejae.tenniverse.persentation.home.user.profile

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.ActivityUserProfileBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.login.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity: BaseActivity<ActivityUserProfileBinding>(R.layout.activity_user_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var currentPermission: String

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onResult)
    private fun onResult(result: ActivityResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                result.data?.let { img ->
                    val uri = img.data ?: return
                    viewModel.setProfileImg(uri)
                }
            }
        }
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 권한 허용
        if (isGranted) {
            goGallery()
        }
    }

    override fun initView() {
        initBinding()
        setListener()
        getPermission()
    }

    private fun setListener() {
        binding.ivProfileImg.setOnClickListener {
            if (!checkPermission()) {
                requestPermission.launch(currentPermission)
            } else {
                goGallery()
            }
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }

        binding.btnStandard.setOnClickListener {
            viewModel.updateUser {
                setResult()
            }
        }
    }

    fun initBinding() {
        binding.viewModel = viewModel
        binding.glide = Glide.with(this)
    }

    private fun goGallery() {

        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        val resolveInfoList: List<ResolveInfo?> =
            packageManager.queryIntentActivities(intent, 0)

        resolveInfoList.forEach {
            it?.let { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                if(packageName in listOf(
                        ProfileFragment.SAMSUNG_GALLERY,
                        ProfileFragment.ANDROID_GALLERY,
                        ProfileFragment.GOOGLE_PHOTO
                    ))
                {
                    intent.component =
                        ComponentName(packageName, resolveInfo.activityInfo.name)
                    return@forEach
                }
            }
        }

        activityResultLauncher.launch(intent)
    }

    private fun checkPermission() =
        ContextCompat.checkSelfPermission(
            this,
            currentPermission
        ) == PackageManager.PERMISSION_GRANTED

    private fun getPermission() {
        currentPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    override fun setViewModel() = viewModel
}
