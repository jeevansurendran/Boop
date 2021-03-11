package com.silverpants.instantaneous.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.*
import com.silverpants.instantaneous.R
import com.silverpants.instantaneous.databinding.FragmentAuthOtpBinding
import com.silverpants.instantaneous.misc.AUTH_OTP_TIMEOUT
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AuthOtpFragment : Fragment(R.layout.fragment_auth_otp) {

    private val args: AuthOtpFragmentArgs by navArgs()
    val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var countDownTimer: CountDownTimer

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val phoneNumber = args.phoneNumber

        val binding = FragmentAuthOtpBinding.bind(view)
        val btnAuthOtpNext = binding.btnAuthOtpNext
        val btnAuthOtpResend = binding.btnAuthOtpResend
        val tvAuthOtpCounter = binding.tvAuthOtpCounter
        val etAuthOtpOtp = binding.etAuthOtpOtp

        countDownTimer = object : CountDownTimer(AUTH_OTP_TIMEOUT * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvAuthOtpCounter.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                tvAuthOtpCounter.text = ""
                authViewModel.setOtpState(AuthViewModel.OtpStates.REFRESH_ALLOWED)
            }
        }

        /* next button */
        btnAuthOtpNext.setOnClickListener {
            val code = etAuthOtpOtp.text
            if (code.length != 6) {
                Toast.makeText(
                    requireContext(),
                    "Invalid OTP, OTP length must be 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (authViewModel.verificationId.isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "OTP not yet sent, wait until you receive an OTP",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val credential =
                PhoneAuthProvider.getCredential(authViewModel.verificationId, code.toString())
            authViewModel.credential = credential
            signInWithPhoneAuthCredential()
        }
        /* */
        btnAuthOtpResend.setOnClickListener {
            authViewModel.setOtpState(AuthViewModel.OtpStates.READY)
        }

        authViewModel.otpState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    /* This state makes the request */
                    AuthViewModel.OtpStates.READY -> {
                        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
                        // trigger this whenever in ready state
                        val builder = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(AUTH_OTP_TIMEOUT, TimeUnit.SECONDS)
                            .setActivity(requireActivity())
                            .setCallbacks((requireActivity() as AuthActivity).callbacks)


                        // whenever in ready state the new builder is created
                        if (authViewModel.refreshToken != null) {
                            builder.setForceResendingToken(authViewModel.refreshToken!!)
                        }

                        //disable the buttons
                        btnAuthOtpResend.isEnabled = false
                        btnAuthOtpNext.isEnabled = false

                        // call verify
                        authViewModel.verifyPhoneNumber(builder.build())
                    }
                    AuthViewModel.OtpStates.AUTO_VERIFICATION_COMPLETE -> {
                        signInWithPhoneAuthCredential()
                    }
                    /* This state is for when the code is sent */
                    AuthViewModel.OtpStates.CODE_SENT -> {
                        backPressedCallback.remove()
                        // start timer
                        if (this::countDownTimer.isInitialized) {
                            countDownTimer.start()
                        }

                        // button control
                        btnAuthOtpResend.isEnabled = false
                        btnAuthOtpNext.isEnabled = true
                    }
                    /* This state is when refresh is allowed */
                    AuthViewModel.OtpStates.REFRESH_ALLOWED -> {
                        backPressedCallback.remove()
                        btnAuthOtpResend.isEnabled = true
                        btnAuthOtpNext.isEnabled = true
                    }
                    else -> {
                        backPressedCallback.remove()
                        //disable the buttons
                        btnAuthOtpResend.isEnabled = true
                        btnAuthOtpNext.isEnabled = true
                    }
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredential() {
        val action = AuthOtpFragmentDirections.signin()
        findNavController().navigate(action)
        authViewModel.setOtpState(AuthViewModel.OtpStates.VERIFY_START)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        backPressedCallback.remove()
    }
}