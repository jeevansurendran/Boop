package com.silverpants.instantaneous.data.room

import androidx.annotation.WorkerThread
import com.silverpants.instantaneous.data.room.models.Room
import com.silverpants.instantaneous.data.room.sources.RoomDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import com.silverpants.instantaneous.misc.Result
import kotlinx.coroutines.flow.map

@Singleton
class RoomRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {
    @WorkerThread
    fun getObservableRoom(roomID: String) : Flow<Result<Room>> {
        return roomDataSource.getObservableRoom(roomID).map { room -> Result.Success(room) }
    }
}