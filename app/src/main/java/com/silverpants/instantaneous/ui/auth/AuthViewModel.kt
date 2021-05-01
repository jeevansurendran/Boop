package com.silverpants.instantaneous.ui.auth

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.domain.user.*
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val updateNameUseCase: UpdateNameUseCase,
    private val observableUserInfoUseCase: ObservableUserInfoUseCase,
    private val verifyUserExistsUseCase: VerifyUserExistsUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationUseCase: NotificationUseCase,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {
    var verificationId: String = ""
    var refreshToken: PhoneAuthProvider.ForceResendingToken? = null
    var credential: PhoneAuthCredential? = null

    private val _otpState = MutableLiveData(OtpStates.READY)
    val otpState = _otpState as LiveData<OtpStates>

    val userInfo by lazy { observableUserInfoUseCase(Unit).asLiveData() }

    private val _signInAttemptResult = MutableLiveData<Result<UserState>>()
    val signInAttemptResult: LiveData<Result<UserState>> = _signInAttemptResult

    private val _updateRequest = MutableLiveData<Result<Unit>>(Result.Loading)
    val updateRequest = _updateRequest as LiveData<Result<Unit>>

    private val _postUserId = MutableLiveData<Result<Unit?>>(Result.Loading)
    val postUserId = _postUserId as LiveData<Result<Unit?>>

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

    @SuppressLint("NullSafeMutableLiveData")
    fun postUserId(userId: String) {
        viewModelScope.launch {
            _postUserId.value =
                createUserUseCase(
                    Triple(
                        userId,
                        userInfo.value?.data?.getUid()!!,
                        userInfo.value?.data?.getPhoneNumber()!! to userInfo.value?.data?.getDisplayName()!!
                    )
                )
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun attemptSignIn(credential: AuthCredential) {
        viewModelScope.launch {
            _signInAttemptResult.value = verifyUserExistsUseCase(credential)
        }
    }

    fun setNotificationToken() {
        viewModelScope.launch {
            try {
                val token = firebaseMessaging.token.suspendAndWait()
                notificationUseCase(token)
            } catch (e: Error) {
                firebaseCrashlytics.recordException(e)
            }
        }
    }

    enum class OtpStates {
        READY,
        CODE_SENT,
        REFRESH_ALLOWED,
        AUTO_VERIFICATION_COMPLETE,
        VERIFY_START,
        VERIFY_COMPLETE,
        VERIFY_COMPLETE_NEW_USER,
        VERIFY_FAILED
    }
}