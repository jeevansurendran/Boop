package com.silverpants.instantaneous.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.domain.chat.GetChatFlowCase
import com.silverpants.instantaneous.domain.chat.GetChatMessagesFlowCase
import com.silverpants.instantaneous.domain.user.GetAnotherUserFlowCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data

class ChatViewModel @ViewModelInject constructor(
    private val getAnotherUserFlowCase: GetAnotherUserFlowCase,
    private val getChatFlowCase: GetChatFlowCase,
    private val observableUserUseCase: ObservableUserUseCase,
    private val getChatMessagesFlowCase: GetChatMessagesFlowCase
) : ViewModel() {
    private val _chatId = MutableLiveData<String>()
    val chatId: LiveData<String> = _chatId

    val user by lazy { observableUserUseCase(Unit).asLiveData() }

    private val chat by lazy {
        Transformations.switchMap(chatId) {
            try {
                getChatFlowCase(it!! to user.value?.data?.userId!!).asLiveData()
            } catch (e: Exception) {
                MutableLiveData(Result.Error(e))
            }
        }
    }

    val anotherUser: LiveData<Result<AnotherUser>> by lazy {
        Transformations.switchMap(chat) {
            it?.let {
                return@switchMap when (it) {
                    is Result.Success -> {
                        try {
                            getAnotherUserFlowCase(it.data.getAnotherUserId()).asLiveData()
                        } catch (e: Exception) {
                            MutableLiveData(Result.Error(e))
                        }
                    }
                    is Result.Loading -> {
                        MutableLiveData(Result.Loading)
                    }
                    is Result.Error -> {
                        MutableLiveData(Result.Error(it.exception))
                    }
                }
            }
            MutableLiveData(Result.Loading)
        }
    }

    val chatMessages: LiveData<Result<List<Message>>> by lazy {
        Transformations.switchMap(chat) {
            try {
                return@switchMap when (it) {
                    is Result.Success -> {
                        getChatMessagesFlowCase(it.data.chatId to user.value?.data?.userId!!).asLiveData()
                    }
                    is Result.Loading -> {
                        MutableLiveData(Result.Loading)
                    }
                    is Result.Error -> {
                        MutableLiveData(Result.Error(it.exception))
                    }
                }
            } catch (e: java.lang.Exception) {
                MutableLiveData(Result.Error(e))
            }
        }
    }

    fun setChatId(chatId: String) {
        _chatId.value = chatId
    }

}