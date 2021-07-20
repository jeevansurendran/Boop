package com.silverpants.instantaneous.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.silverpants.instantaneous.domain.user.IsUserDataExistsUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserInfoUseCase
import com.silverpants.instantaneous.misc.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
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
