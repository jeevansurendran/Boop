package com.silverpants.instantaneous.ui.chat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.silverpants.instantaneous.domain.room.ObservableRoomUseCase
import com.silverpants.instantaneous.misc.data

class RoomViewModel @ViewModelInject constructor(
    private val observableRoomUseCase: ObservableRoomUseCase
) : ViewModel() {
    private val roomID = MutableLiveData<String>()

    private val roomResult = roomID.switchMap {
        return@switchMap observableRoomUseCase(it).asLiveData()
    }

    val data = roomResult.map {
        it.data!!
    }


    fun setRoomID(id: String) {
        roomID.value = id
    }
}