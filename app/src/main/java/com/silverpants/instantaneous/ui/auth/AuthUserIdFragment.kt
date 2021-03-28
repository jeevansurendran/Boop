package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthUserIdBinding
import com.silverpants.instantaneous.misc.DocumentExistsException
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import com.silverpants.instantaneous.misc.toast
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthUserIdFragment : Fragment(R.layout.fragment_auth_user_id) {

    private val viewModel by activityViewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthUserIdBinding.bind(view)
        val tilAuthUserId = binding.tilAuthUserId
        val btnAuthUserIdNext = binding.btnAuthUserIdNext

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
            }
        }

        btnAuthUserIdNext.setOnClickListener {
            viewModel.userInfo.value?.data?.getUid()?.let { uid ->
                viewModel.postUserId(
                    tilAuthUserId.editText?.text.toString(),
                    uid
                )
            } ?: toast("Please restart your app")
        }
    }
}