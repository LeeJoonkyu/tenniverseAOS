package com.heejae.tenniverse.persentation.login

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.pm.ResolveInfo
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentProfileBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.domain.model.RegisterState
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.util.append8
import com.heejae.tenniverse.persentation.wait.WaitingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var currentPermission: String
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onResult)


    private fun onResult(result: ActivityResult) {
        Log.d("ProfileFragment", "onResult $result")
        when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> {
                Log.d("ProfileFragment", "Result : ${this::class.java} : ${result.data}")

                result.data?.let { img ->
                    val uri = img.data ?: return
                    viewModel.setProfileImg(uri)
                }
                // TODO : null handling
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
        // 권한 허용x
        // TODO : 허용 안함 Handling
//        else {
//            if (shouldShowRequestPermissionRationale(currentPermission)) {
//
//            }
//            else {
//
//            }
//        }
    }

    override fun initView() {
        initBinding()
        setListener()
        getPermission()
        setViewModelObserve()
    }
    private fun setViewModelObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collectLatest { state ->
                    when(state) {
                        is RegisterState.UnInitialized -> {}
                        is RegisterState.Loading -> {
                            progressDialog.showDialog()
                        }
                        is RegisterState.Success -> {
                            progressDialog.closeDialog()
                            requireActivity().apply {
                                startActivity(
                                    Intent(this, WaitingActivity::class.java)
                                )
                                finish()
                            }
                        }
                        is RegisterState.Failure -> {
                            progressDialog.closeDialog()
                            // TODO : error
                        }
                    }
                }
            }
        }
    }

    private fun setListener() {
        binding.ivProfileImg.setOnClickListener {
            if (!checkPermission()) {
                requestPermission.launch(currentPermission)
            } else {
                goGallery()
            }
        }
    }

    private fun initBinding() {
        binding.glide = Glide.with(requireActivity())
        binding.viewModel = viewModel

        binding.tvSuccessProfile.text = buildSpannedString {
            append8(requireActivity(), text = "프로필 생성이 완료되었어요!")
        }
        binding.tvAddProfileImg.text = buildSpannedString {
            append8(requireActivity(), font = R.font.noto_sans_semi_bold, text = "마지막으로, ")
            append8(requireActivity(), font = R.font.noto_sans_semi_bold, color = R.color.green, text = "프로필 사진")
            append8(requireActivity(), font = R.font.noto_sans_semi_bold, text = "을 추가해볼까요?")
        }
    }

    private fun goGallery() {

        val newIntent = Intent(
            Intent.ACTION_GET_CONTENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }

        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }


        val resolveInfoList: List<ResolveInfo?> =
            requireActivity().packageManager.queryIntentActivities(intent, 0)

        resolveInfoList.forEach {
            it?.let { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                if(packageName in listOf(SAMSUNG_GALLERY, ANDROID_GALLERY, GOOGLE_PHOTO))
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
            requireActivity(),
            currentPermission
        ) == PERMISSION_GRANTED

    private fun getPermission() {
        currentPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            READ_MEDIA_IMAGES
        } else {
            READ_EXTERNAL_STORAGE
        }
    }

    companion object {
        const val SAMSUNG_GALLERY = "com.sec.android.gallery3d"
        const val ANDROID_GALLERY = "com.android.gallery"
        const val GOOGLE_PHOTO = "com.google.android.apps.photos"
        fun newInstance() = ProfileFragment()
    }
}