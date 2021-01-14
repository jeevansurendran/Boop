package com.silverpants.instantaneous.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.silverpants.instantaneous.ui.chat.MainActivity
import com.silverpants.instantaneous.misc.USER
import com.silverpants.instantaneous.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}