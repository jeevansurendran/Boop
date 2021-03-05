package com.silverpants.instantaneous.ui.auth

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.domain.user.UpdateNameUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    val updateNameUseCase: UpdateNameUseCase,
    val observableUserUseCase: ObservableUserUseCase
) : ViewModel() {

    var verificationId: String = ""
    var refreshToken: PhoneAuthProvider.ForceResendingToken? = null
    var credential: PhoneAuthCredential? = null
    private val _otpState = MutableLiveData(OtpStates.READY)
    val otpState = _otpState as LiveData<OtpStates>
    val userInfo get() = observableUserUseCase(Unit).asLiveData()

    private val _updateRequest = MutableLiveData<Result<Unit>>(Result.Loading)
    val updateRequest = _updateRequest as LiveData<Result<Unit>>

    fun verifyPhoneNumber(options: PhoneAuthOptions) {
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun setOtpState(state: OtpStates) {
        _otpState.value = state
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setName(name: String) {
        viewModelScope.launch {
            _updateRequest.value = updateNameUseCase(name)
        }

    }

    enum class OtpStates {
        READY,
        CODE_SENT,
        REFRESH_ALLOWED,
        AUTO_VERIFICATION_COMPLETE,
        VERIFY_START,
        VERIFY_COMPLETE,
        VERIFY_FAILED
    }
}