package com.silverpants.instantaneous.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.misc.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.user.observe(this) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        val userId = it.data.userId
                        lifecycle.addObserver(object : LifecycleObserver {
                            @OnLifecycleEvent(Lifecycle.Event.ON_START)
                            fun setUserOnline() {
                                viewModel.setUserOnline(userId)
                            }

                            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                            fun setUserOffline() {
                                viewModel.setUserOffline(userId)
                            }
                        })
                    }
                    else -> {

                    }
                }
            }
        }
    }

    companion object {
        fun launchHome(context: Context) =
            Intent(context, MainActivity::class.java)
    }

}
