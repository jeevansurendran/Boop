package com.silverpants.instantaneous.domain.room

import com.silverpants.instantaneous.data.room.RoomRepository
import com.silverpants.instantaneous.data.room.models.Room
import com.silverpants.instantaneous.di.IoDispatcher
import com.silverpants.instantaneous.domain.FlowUseCase
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservableRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher): FlowUseCase<String, Room>(dispatcher)  {
    override fun execute(parameters: String): Flow<Result<Room>> {
        return roomRepository.getObservableRoom(parameters)
    }
}