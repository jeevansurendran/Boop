package com.silverpants.instantaneous.ui.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.databinding.ActivityLaunchBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.suspendAndWait
import com.silverpants.instantaneous.misc.toast
import com.silverpants.instantaneous.ui.auth.AuthActivity
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModels()
    private lateinit var binding: ActivityLaunchBinding

    @Inject
    lateinit var dynamicLinks: FirebaseDynamicLinks

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        viewModel.isUserDataExists.observe(this) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        var intent: Intent? = null
                        val job = lifecycleScope.launchWhenStarted {
                            try {
                                val pendingDynamicLinkData =
                                    dynamicLinks.getDynamicLink(this@LaunchActivity.intent)
                                        .suspendAndWait()
                                val userId = pendingDynamicLinkData?.link?.let {
                                    val deepLinkUrl = it.toString()
                                    deepLinkUrl.substring(deepLinkUrl.lastIndexOf("/") + 1)
                                }
                                intent = if (it.data != UserState.EXISTS) {
                                    if (userId != null) {
                                        AuthActivity.launchAuthentication(
                                            applicationContext,
                                            userId
                                        )
                                    } else {
                                        AuthActivity.launchAuthentication(applicationContext)
                                    }
                                } else {
                                    MainActivity.launchHome(applicationContext, userId)
                                }
                            } catch (e: Exception) {
                                crashlytics.recordException(e)
                                AuthActivity.launchAuthentication(applicationContext)
                            }

                        }
                        job.invokeOnCompletion {
                            startActivity(intent)
                            finish()
                        }
                    }
                    else -> {
                        toast("There has been an error, please restart the app.")
                    }
                }
            }
        }
    }
}