package com.silverpants.instantaneous.ui.launch

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.silverpants.instantaneous.domain.user.IsUserDataExistsUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserInfoUseCase
import com.silverpants.instantaneous.misc.data
import kotlinx.coroutines.flow.collect

class LaunchViewModel @ViewModelInject constructor(
    val observableUserInfoUseCase: ObservableUserInfoUseCase,
    val isUserDataExistsUseCase: IsUserDataExistsUseCase
) :
    ViewModel() {
    private val userInfo by lazy { observableUserInfoUseCase(Unit) }

    val isUserDataExists by lazy {
        liveData {
            userInfo.collect {
                emit(isUserDataExistsUseCase(it.data?.getUid()))
            }
        }
    }
}
