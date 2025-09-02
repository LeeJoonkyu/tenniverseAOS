package com.heejae.tenniverse.persentation.splash

import android.content.Intent
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.net.Uri
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentSplashBinding
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.home.HomeActivity
import com.heejae.tenniverse.persentation.login.LoginActivity
import com.heejae.tenniverse.persentation.wait.WaitingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val scope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: SplashViewModel by viewModels()
    override fun setViewModel() = viewModel

    override fun initView() {
        viewModel.checkPrevVersion(
            prev = {
                showDialog(DialogType.VERSION_CHECK) {
                    startActivity(
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse("market://details?id=$packageName")
                        }
                    )
                }
            },
            onSuccess = {
                if (auth.uid == null) {
                    delayStartActivity(
                        Intent(this@SplashActivity, LoginActivity::class.java)
                    )
                } else {
                    viewModel.checkUser { isRegistered, isMember ->
                        val intent = if (!isRegistered) {
                            Intent(this@SplashActivity, LoginActivity::class.java)
                        } else {
                            if (isMember) {
                                Intent(this@SplashActivity, HomeActivity::class.java)
                            } else {
                                Intent(this@SplashActivity, WaitingActivity::class.java)
                            }
                        }

                        DEBUG(this@SplashActivity.name, "it")
                        delayStartActivity(intent)
                    }
                }
            }
        )
    }

    private fun delayStartActivity(intent: Intent) {
        scope.launch {
            delay(2000)
            DEBUG(this@SplashActivity.name, "delayStartActivity")
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        overridePendingTransition(R.anim.slide_none, R.anim.slide_to_right)
    }
}