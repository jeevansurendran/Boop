package com.silverpants.instantaneous.ui.auth

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.domain.user.IsUserDataExistsUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserInfoUseCase
import com.silverpants.instantaneous.domain.user.PostUserIdUseCase
import com.silverpants.instantaneous.domain.user.UpdateNameUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val updateNameUseCase: UpdateNameUseCase,
    private val observableUserInfoUseCase: ObservableUserInfoUseCase,
    private val isUserDataExistsUseCase: IsUserDataExistsUseCase,
    private val postUserIdUseCase: PostUserIdUseCase
) : ViewModel() {

    var verificationId: String = ""
    var refreshToken: PhoneAuthProvider.ForceResendingToken? = null
    var credential: PhoneAuthCredential? = null

    private val _otpState = MutableLiveData(OtpStates.READY)
    val otpState = _otpState as LiveData<OtpStates>

    val userInfo by lazy { observableUserInfoUseCase(Unit).asLiveData() }

    val isFirestoreUserDataExists by lazy {
        Transformations.switchMap(userInfo) {
            liveData { emit(isUserDataExistsUseCase(it.data?.getUid())) }
        }
    }

    private val _updateRequest = MutableLiveData<Result<Unit>>(Result.Loading)
    val updateRequest = _updateRequest as LiveData<Result<Unit>>

    private val _postUserId = MutableLiveData<Result<FirestoreUserInfo?>>(Result.Loading)
    val postUserId = _postUserId as LiveData<Result<FirestoreUserInfo?>>

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
    fun postUserId(userId: String, uid: String) {
        viewModelScope.launch {
            _postUserId.value =
                postUserIdUseCase(Triple(userId, uid, userInfo.value?.data?.getPhoneNumber()!!))
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