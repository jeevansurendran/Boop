package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
                        authViewModel.attemptSignIn(credential)
                    }
                    AuthViewModel.OtpStates.VERIFY_COMPLETE_NEW_USER -> {
                        proceedToOnBoarding()
                    }
                    AuthViewModel.OtpStates.VERIFY_COMPLETE -> {
                        authViewModel.setNotificationToken()
                        proceedToOnBoarding()
                    }
                    AuthViewModel.OtpStates.VERIFY_FAILED -> {
                        backPressedCallback.remove()
                    }
                    else -> {

                    }
                }
            }
        }
        authViewModel.signInAttemptResult.observe(viewLifecycleOwner)
        {
            when (authViewModel.otpState.value) {
                AuthViewModel.OtpStates.VERIFY_START -> {
                    it?.let {
                        when (it) {
                            is Result.Success -> {
                                when (it.data) {
                                    UserState.NO_DATA -> {
                                        authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_COMPLETE_NEW_USER)
                                    }
                                    UserState.EXISTS -> {
                                        authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_COMPLETE)
                                    }
                                    UserState.NOT_EXIST -> {
                                    }
                                }

                            }
                            is Result.Error -> {
                                authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_FAILED)
                                if (it.exception is FirebaseAuthInvalidCredentialsException) {
                                    toast("Invalid code try again")
                                } else {
                                    toast("There seems to be and error try again")
                                }
                                findNavController().popBackStack()
                            }
                            else -> {
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun proceedToOnBoarding() {
        val action = AuthLoadingFragmentDirections.startOnboarding()
        findNavController().navigate(action)
        backPressedCallback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
    }

}