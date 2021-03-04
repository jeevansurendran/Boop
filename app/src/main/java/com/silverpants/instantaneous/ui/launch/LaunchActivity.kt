package com.silverpants.instantaneous.ui.launch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.silverpants.instantaneous.databinding.ActivityLaunchBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.ui.auth.AuthActivity
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModels()
    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        viewModel.userInfo.observe(this) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        Timber.d("Hmmmmmmm ${it.data.getDisplayName()}")
                        lifecycleScope.launchWhenStarted {
                            delay(1000)
                            if (!it.data.isSignedIn()) {
                                val intent = AuthActivity.launchAuthenitcation(applicationContext)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = MainActivity.launchHome(applicationContext)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "There has been an error, please restart your app.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}