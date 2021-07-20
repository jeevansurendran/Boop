package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthUserIdBinding
import com.silverpants.instantaneous.misc.DocumentExistsException
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthUserIdFragment : Fragment(R.layout.fragment_auth_user_id) {

    private val viewModel by activityViewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthUserIdBinding.bind(view)
        val tilAuthUserId = binding.tilAuthUserId
        val btnAuthUserIdNext = binding.btnAuthUserIdNext

        val userIdPattern: Regex = "[a-z_0-9]{3,}".toRegex()

        viewModel.postUserId.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        val intent = MainActivity.launchHome(requireContext())
                        viewModel.setNotificationToken()
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is Result.Error -> {
                        if (it.exception is DocumentExistsException) {
                            setInvalidFormat(
                                tilAuthUserId, btnAuthUserIdNext,
                                "This user id is already taken"
                            )
                        }
                    }
                    else -> {

                    }
                }
                btnAuthUserIdNext.isEnabled = true
            }
        }

        btnAuthUserIdNext.setOnClickListener {
            if (!tilAuthUserId.editText?.text.toString().matches(userIdPattern)) {
                setInvalidFormat(tilAuthUserId, btnAuthUserIdNext)
                return@setOnClickListener
            }
            btnAuthUserIdNext.isEnabled = false
            viewModel.postUserId(tilAuthUserId.editText?.text.toString())
        }

        binding.tilAuthUserId.editText?.doOnTextChanged { text, start, before, count ->
            if (!tilAuthUserId.editText?.text.toString().matches(userIdPattern)) {
                setInvalidFormat(tilAuthUserId, btnAuthUserIdNext)
                return@doOnTextChanged
            }
            setValidFormat(tilAuthUserId, btnAuthUserIdNext)
        }
    }

    private fun setInvalidFormat(
        tilAuth: TextInputLayout,
        btn: Button,
        message: String = "User Id can only contain lowercase letters, underscores and must be longer than 3 characters."
    ) {
        tilAuth.error = message
        btn.isEnabled = false
    }

    private fun setValidFormat(tilAuth: TextInputLayout, btn: Button) {
        tilAuth.error = null
        btn.isEnabled = true
    }
}