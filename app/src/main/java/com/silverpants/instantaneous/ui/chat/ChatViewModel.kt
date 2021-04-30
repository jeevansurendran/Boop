package com.silverpants.instantaneous.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.silverpants.instantaneous.data.chat.model.Chat
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.data.chat.model.Messages
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.domain.chat.GetChatFlowCase
import com.silverpants.instantaneous.domain.chat.GetChatMessagesFlowCase
import com.silverpants.instantaneous.domain.chat.PostImmediateMessageUseCase
import com.silverpants.instantaneous.domain.chat.PostMessageUseCase
import com.silverpants.instantaneous.domain.user.GetAnotherUserFlowCase
import com.silverpants.instantaneous.domain.user.ObservableUserUseCase
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChatViewModel @ViewModelInject constructor(
    private val getAnotherUserFlowCase: GetAnotherUserFlowCase,
    private val getChatFlowCase: GetChatFlowCase,
    private val observableUserUseCase: ObservableUserUseCase,
    private val getChatMessagesFlowCase: GetChatMessagesFlowCase,
    private val postMessageUseCase: PostMessageUseCase,
    private val postImmediateMessageUseCase: PostImmediateMessageUseCase
) : ViewModel() {
    private val _chatId = MutableLiveData<String>()
    val chatId: LiveData<String> = _chatId

    val user by lazy { observableUserUseCase(Unit).asLiveData() }

    private val chatFlow: Flow<Result<Chat>> by lazy {
        chatId.asFlow().flatMapLatest {
            getChatFlowCase(it to user.value?.data?.userId!!)
        }
    }
    val chat by lazy {
        chatFlow.asLiveData()
    }

    private val _chatResult = MutableLiveData<Result<Message>>()
    val chatResult: LiveData<Result<Message>> = _chatResult

    val anotherUser: LiveData<Result<AnotherUser>> by lazy {
        liveData {
            chatFlow.first().let {
                emitSource(
                    when (it) {
                        is Result.Success -> {
                            try {
                                getAnotherUserFlowCase(it.data.getReceiversUserId()).asLiveData()
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
                )
            }
        }
    }

    val chatMessages: LiveData<Result<Messages>> by lazy {
        Transformations.switchMap(chatId) {
            return@switchMap try {
                getChatMessagesFlowCase(it to user.value?.data?.userId!!).asLiveData()
            } catch (e: Exception) {
                MutableLiveData(Result.Error(e))
            }
        }
    }

    fun setChatId(chatId: String) {
        _chatId.value = chatId
    }

    fun postMessage(message: String) {
        viewModelScope.launch {
            if (chat.value !== null &&
                chat.value !== null &&
                chat.value!!.data !== null &&
                user.value !== null &&
                user.value?.data !== null
            ) {
                _chatResult.value = postMessageUseCase(
                    Triple(
                        chatId.value!!,
                        message,
                        user.value?.data?.userId!! to chat.value?.data?.sendersUserIndex!!
                    )
                )
            }
        }
    }

    fun postImmediateMessage(message: String) {
        viewModelScope.launch {
            if (chat.value !== null &&
                chat.value !== null &&
                chat.value!!.data !== null &&
                user.value !== null &&
                user.value?.data !== null
            ) {
                postImmediateMessageUseCase(
                    Triple(
                        chatId.value!!,
                        message,
                        chat.value?.data?.sendersUserIndex!!
                    )
                )
            }
        }
    }
}