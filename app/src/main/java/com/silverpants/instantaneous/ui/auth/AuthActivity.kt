package com.silverpants.instantaneous.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.silverpants.instantaneous.databinding.ActivityAuthBinding
import com.silverpants.instantaneous.misc.toast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    internal lateinit var firebaseCrashlytics: FirebaseCrashlytics

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            authViewModel.credential = credential
            authViewModel.setOtpState(AuthViewModel.OtpStates.AUTO_VERIFICATION_COMPLETE)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Timber.e(exception)
            toast("There seems to be an error, Try again later! ${exception.message}")
            firebaseCrashlytics.recordException(exception)
            authViewModel.setOtpState(AuthViewModel.OtpStates.START)
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
        authViewModel.sharersUserId = intent.getStringExtra(USER_ID_KEY)
        setContentView(binding.root)
    }

    companion object {
        private const val USER_ID_KEY = "user.id.key"
        fun launchAuthentication(context: Context) = Intent(context, AuthActivity::class.java)

        fun launchAuthentication(context: Context, userId: String) =
            Intent(context, AuthActivity::class.java).apply {
                putExtra(USER_ID_KEY, userId)
            }
    }

}