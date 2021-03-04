package com.silverpants.instantaneous.ui.launch

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase

class LaunchViewModel @ViewModelInject constructor(val observableUserUseCase: ObservableUserUseCase) :
    ViewModel() {
    val userInfo get() = observableUserUseCase(Unit).asLiveData()
}