package com.silverpants.instantaneous.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthOtpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AuthOtpFragment : Fragment(R.layout.fragment_auth_otp) {

    val args: AuthOtpFragmentArgs by navArgs()
    val authViewModel: AuthViewModel by activityViewModels()
    lateinit var verificationId: String
    lateinit var refreshToken: PhoneAuthProvider.ForceResendingToken

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val phoneNumber = args.phoneNumber

        val binding = FragmentAuthOtpBinding.bind(view)
        val btnAuthOtpNext = binding.btnAuthOtpNext
        val btnAuthOtpResend = binding.btnAuthOtpResend
        val tvAuthOtpCounter = binding.tvAuthOtpCounter
        val etAuthOtpOtp = binding.etAuthOtpOtp

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {

                } else if (e is FirebaseTooManyRequestsException) {

                } else {

                }
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@AuthOtpFragment.verificationId = verificationId
                refreshToken = forceResendingToken
            }

        }

        authViewModel.otpState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    AuthViewModel.OtpStates.READY -> {
                        // trigger this whenever in ready state
                        val builder = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(requireActivity())
                            .setCallbacks(callbacks)

                        if (this::refreshToken.isInitialized) {
                            builder.setForceResendingToken(refreshToken)
                        }

                        //disable the buttons
                        btnAuthOtpResend.isEnabled = false;

                        // call verify
                        authViewModel.verifyPhoneNumber(builder.build())
                    }
                    AuthViewModel.OtpStates.CODE_SENT -> {

                    }
                    AuthViewModel.OtpStates.VERIFY_COMPLETE -> {

                    }
                    AuthViewModel.OtpStates.VERIFY_FAILED -> {

                    }
                }
            }
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val action = AuthOtpFragmentDirections.signin(credential)
        findNavController().navigate(action)

    }
}