package com.silverpants.instantaneous.data.room.sources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.room.models.Room
import com.silverpants.instantaneous.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    @ExperimentalCoroutinesApi
    fun getObservableRoom(roomID: String): Flow<Room> {
        return if (roomID.isEmpty()) {
            flow {
                emit(Room())
            }
        } else {
            (channelFlow<Room> {
                val roomDocument = firestore
                    .collection(ROOM_COLLECTION)
                    .document(roomID)

                val subscription = roomDocument.addSnapshotListener { snapshot, _ ->
                    if (snapshot == null) {
                        return@addSnapshotListener
                    }
                    val userEvent = if (snapshot.exists()) {
                        roomParser(snapshot)
                    } else {
                        Room()
                    }
                    channel.offer(userEvent)
                }
                awaitClose { subscription.remove() }
            }).flowOn((ioDispatcher))
        }
    }

    private fun roomParser(snapshot: DocumentSnapshot): Room {
        return Room(
            message1 = snapshot["message1"] as? String ?: "",
            message2 = snapshot["message2"] as? String ?: "",
            user1 = snapshot["user1"] as? String ?: "",
            user2 = snapshot["user2"] as? String ?: ""
        )
    }

    companion object {
        private const val ROOM_COLLECTION = "rooms"
    }
}