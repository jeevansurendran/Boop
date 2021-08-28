package com.silverpants.instantaneous.ui.recent

import androidx.lifecycle.*
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.domain.chat.GetRecentChatFlowUseCase
import com.silverpants.instantaneous.domain.share.ShareAppUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentChatViewModel @Inject constructor(
    private val observableUserUseCase: ObservableUserUseCase,
    private val getRecentChatFlowUseCase: GetRecentChatFlowUseCase,
    private val shareAppUseCase: ShareAppUseCase,
) : ViewModel() {

    val user by lazy {
        observableUserUseCase(Unit).asLiveData()
    }
    private val _shareUserLiveData = MutableLiveData<Result<String>>()
    val shareUserLiveData = _shareUserLiveData as LiveData<Result<String>>

    val recentChat: LiveData<Result<List<RecentChat>>> by lazy {
        user.switchMap {
            if (it.data == null) {
                return@switchMap MutableLiveData(Result.Loading)
            }
            getRecentChatFlowUseCase(it.data?.userId!!).asLiveData()
        }
    }

    fun shareApp() {
        viewModelScope.launch {
            when (val result = shareAppUseCase(Unit)) {
                is Result.Success -> {
                    _shareUserLiveData.value = result
                }
                else -> {
                    _shareUserLiveData.value = Result.Error(Exception("Unknown error occured"))
                }
            }
        }
    }
}