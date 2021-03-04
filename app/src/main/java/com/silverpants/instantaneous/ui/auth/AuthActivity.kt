package com.silverpants.instantaneous.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.silverpants.instantaneous.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            authViewModel.credential = credential
            authViewModel.setOtpState(AuthViewModel.OtpStates.AUTO_VERIFICATION_COMPLETE)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
        }


        override fun onCodeSent(
            verificationId: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            authViewModel.verificationId = verificationId
            authViewModel.refreshToken = forceResendingToken
            authViewModel.setOtpState(AuthViewModel.OtpStates.CODE_SENT)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        fun launchAuthenitcation(context: Context) = Intent(context, AuthActivity::class.java)
    }

}