package com.silverpants.instantaneous.ui.launch

import android.content.Intent
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
        viewModel.isFirestoreUserDataExists.observe(this) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        var intent: Intent? = null
                        val job = lifecycleScope.launchWhenStarted {
                            intent = if (!it.data) {
                                AuthActivity.launchAuthentication(applicationContext)
                            } else {
                                MainActivity.launchHome(applicationContext)
                            }
                        }
                        job.invokeOnCompletion {
                            startActivity(intent)
                            finish()
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