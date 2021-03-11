package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthUserIdBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthUserIdFragment : Fragment(R.layout.fragment_auth_user_id) {

    val viewModel by activityViewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthUserIdBinding.bind(view)
        val tilAuthUserId = binding.tilAuthUserId
        val btnAuthUserIdNext = binding.btnAuthUserIdNext

        viewModel.updateNameUseCase


    }
}