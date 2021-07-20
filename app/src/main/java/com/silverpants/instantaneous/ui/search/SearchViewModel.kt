package com.silverpants.instantaneous.ui.search

import androidx.lifecycle.*
import com.silverpants.instantaneous.domain.chat.GetChatIdUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserSearchCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject
constructor(
    private val observableUserSearchCase: ObservableUserSearchCase,
    private val observableUserUseCase: ObservableUserUseCase,
    private val getChatIdUseCase: GetChatIdUseCase
) : ViewModel() {

    private val _query = MutableLiveData("")

    private val user by lazy {
        observableUserUseCase(Unit)
    }

    val result by lazy {
        observableUserSearchCase(_query.asFlow().debounce(100L)).asLiveData()
    }

    fun setQueryString(query: String) {
        _query.value = query
    }

    fun getChatId(user2: String): LiveData<Result<String>> {
        return liveData {
            user.collect {
                val user1 = it.data?.userId
                if (user1.isNullOrEmpty()) {
                    return@collect
                }
                emit(getChatIdUseCase(user1 to user2))
            }
        }
    }
}