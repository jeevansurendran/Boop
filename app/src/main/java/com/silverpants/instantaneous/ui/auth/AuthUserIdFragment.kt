package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Button
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
        val userId = tilAuthUserId.editText?.text.toString().trim()
        val userIdPattern: Regex = "[a-z][a-z_0-9]".toRegex()

        viewModel.postUserId.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        val intent = MainActivity.launchHome(requireContext())
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is Result.Error -> {
                        if (it.exception is DocumentExistsException) {
                            tilAuthUserId.error =
                                "This user id is already taken, Try with another one"
                        }
                    }
                    else -> {

                    }
                }
                btnAuthUserIdNext.isEnabled = true
            }
        }

        btnAuthUserIdNext.setOnClickListener {
            if (!userId.matches(userIdPattern)) {

            }
            btnAuthUserIdNext.isEnabled = false
            viewModel.postUserId(userId)
        }

        tilAuthUserId.editText?.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                return source?.filter { source.subSequence(start, end).matches(userIdPattern) }
            }

        })

    }

    fun setInvalidFormat(tilAuth: TextInputLayout, btn: Button) {
        tilAuth.editText?.error = "User Id can only contain small letters, alphabets and "

    }
}