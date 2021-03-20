package com.silverpants.instantaneous.ui.launch

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.silverpants.instantaneous.domain.user.IsFirestoreUserDataExistsUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserInfoUseCase
import com.silverpants.instantaneous.misc.data

class LaunchViewModel @ViewModelInject constructor(
    val observableUserInfoUseCase: ObservableUserInfoUseCase,
    val isFirestoreUserDataExistsUseCase: IsFirestoreUserDataExistsUseCase
) :
    ViewModel() {
    private val userInfo by lazy { observableUserInfoUseCase(Unit).asLiveData() }

    val isFirestoreUserDataExists by lazy {
        Transformations.switchMap(userInfo) {
            liveData { emit(isFirestoreUserDataExistsUseCase(it.data?.getUid())) }
        }
    }
}
