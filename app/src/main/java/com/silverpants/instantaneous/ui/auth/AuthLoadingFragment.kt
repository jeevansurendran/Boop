package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.misc.data
import com.silverpants.instantaneous.misc.suspendAndWait
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthLoadingFragment : Fragment(R.layout.fragment_auth_loading) {

    private val authViewModel: AuthViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Toast.makeText(
                requireContext(),
                "Please Wait until verification is complete",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val credential = authViewModel.credential!!

        authViewModel.otpState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    AuthViewModel.OtpStates.VERIFY_START -> {
                        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
                        lifecycleScope.launch {
                            try {
                                val result = auth.signInWithCredential(credential).suspendAndWait()
                                if (result.additionalUserInfo?.isNewUser == true || authViewModel.isFirestoreUserDataExists.value?.data == false) {
                                    authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_COMPLETE_NEW_USER)
                                    return@launch
                                }
                                authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_COMPLETE)
                            } catch (e: FirebaseException) {
                                authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_FAILED)
                                if (e is FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Invalid code try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                    AuthViewModel.OtpStates.VERIFY_COMPLETE_NEW_USER, AuthViewModel.OtpStates.VERIFY_COMPLETE -> {
                        val action = AuthLoadingFragmentDirections.startOnboarding()
                        findNavController().navigate(action)
                        backPressedCallback.remove()
                    }
                    AuthViewModel.OtpStates.VERIFY_FAILED -> {
                        backPressedCallback.remove()
                    }
                    else -> {

                    }
                }
            }
        }
        authViewModel.isFirestoreUserDataExists.observe(viewLifecycleOwner) {
            // empty observer so that data is fetched atleast once
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
    }

}