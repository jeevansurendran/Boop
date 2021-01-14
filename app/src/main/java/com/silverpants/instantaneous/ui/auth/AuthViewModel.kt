package com.silverpants.instantaneous.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.ticker

class AuthViewModel @ViewModelInject constructor() : ViewModel() {

    fun verifyPhoneNumber(options: PhoneAuthOptions) {
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val _otpState = MutableLiveData(OtpStates.READY)

    val otpState = _otpState as LiveData<OtpStates>

    enum class OtpStates {
        READY,
        CODE_SENT,
        VERIFY_COMPLETE,
        VERIFY_FAILED
    }

    fun setOtpState(state: OtpStates) {
        _otpState.value = state
    }

}