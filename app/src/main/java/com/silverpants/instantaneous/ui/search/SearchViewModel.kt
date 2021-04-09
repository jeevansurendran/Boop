package com.silverpants.instantaneous.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.silverpants.instantaneous.domain.chat.GetChatIdUseCase
import com.silverpants.instantaneous.domain.user.ObservableUserSearchCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

class SearchViewModel @ViewModelInject
constructor(
    private val observableUserSearchCase: ObservableUserSearchCase,
    private val observableUserUseCase: ObservableUserUseCase,
    private val getChatIdUseCase: GetChatIdUseCase
) : ViewModel() {

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

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