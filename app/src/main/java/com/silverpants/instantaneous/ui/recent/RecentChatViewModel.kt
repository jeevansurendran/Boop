package com.silverpants.instantaneous.ui.recent

import androidx.lifecycle.*
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.domain.chat.GetRecentChatFlowUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentChatViewModel @Inject constructor(
    private val observableUserUseCase: ObservableUserUseCase,
    private val getRecentChatFlowUseCase: GetRecentChatFlowUseCase,
) : ViewModel() {

    val user by lazy {
        observableUserUseCase(Unit).asLiveData()
    }

    val recentChat: LiveData<Result<List<RecentChat>>> by lazy {
        user.switchMap {
            if (it.data == null) {
                return@switchMap MutableLiveData(Result.Loading)
            }
            getRecentChatFlowUseCase(it.data?.userId!!).asLiveData()
        }
    }
}