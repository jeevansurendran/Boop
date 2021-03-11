package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthOnboardingBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthOnboardingFragment : Fragment(R.layout.fragment_auth_onboarding) {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAuthOnboardingBinding.bind(view)
        val etAuthOnboardingName = binding.etAuthOnboardingName
        val btnAuthOnboardingNext = binding.btnAuthOnboardingNext

        authViewModel.userInfo.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        etAuthOnboardingName.setText(it.data.getDisplayName())
                    }
                    else -> {
                        etAuthOnboardingName.setText("")
                    }
                }
            }
        }

        authViewModel.updateRequest.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    is Result.Success -> {
                        if (authViewModel.otpState.value === AuthViewModel.OtpStates.VERIFY_COMPLETE_NEW_USER) {
                            val action = AuthOnboardingFragmentDirections.acceptUserId()
                            findNavController().navigate(action)
                        } else {
                            val intent = MainActivity.launchHome(requireContext())
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {

                    }
                }
            }
        }
        btnAuthOnboardingNext.setOnClickListener {
            authViewModel.setName(etAuthOnboardingName.text.toString())
        }
    }
}