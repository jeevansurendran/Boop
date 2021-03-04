package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthOnboardingBinding
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.ui.chat.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthOnboardingFragment : Fragment(R.layout.fragment_auth_onboarding) {

    @Inject
    lateinit var auth: FirebaseAuth

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
                        val intent = MainActivity.launchHome(requireContext())
                        startActivity(intent)
                        Timber.d("helllo sir my name is cool kid")
                        requireActivity().finish()
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
            Timber.d("heelksjdflkjsflk")
            authViewModel.setName(etAuthOnboardingName.text.toString())
        }
    }
}