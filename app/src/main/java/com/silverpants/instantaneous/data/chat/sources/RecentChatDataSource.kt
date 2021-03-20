package com.silverpants.instantaneous.data.chat.sources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.chat.model.RecentChat
import com.silverpants.instantaneous.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class RecentChatDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    fun getObservableRecentChat(userId: String): Flow<List<RecentChat>> {
        // User id is empty hence return
        if (userId.isEmpty()) {
            flow {
                emit(emptyList<RecentChat>())
            }
        }
        val chatFlow = channelFlow<List<DocumentSnapshot>> {
            val chatCollection = firestore
                .collection(CHATS_COLLECTION)
                .whereArrayContains(USERS_FIELD, userId)


            val subscription = chatCollection.addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    return@addSnapshotListener
                }
                if (snapshot.isEmpty) {
                    return@addSnapshotListener
                }
                val users = snapshot.map {
                    val list: List<String> = it[USERS_FIELD] as List<String>
                    if (list[0] == userId) list[1] else list[0]
                }.toSet()

                if (users.isEmpty()) {
                    channel.offer(emptyList())
                    return@addSnapshotListener
                }
                channel.offer(snapshot.documents)
            }
            awaitClose { subscription.remove() }
        }
        return chatFlow.flatMapLatest { chatList ->
            // create a list of flow<RecentChat>
            val flowList = chatList.map { chat ->
                val list: List<String> = chat[USERS_FIELD] as List<String>
                val chatUserId = if (list[0] == userId) list[1] else list[0]
                channelFlow<RecentChat> {
                    val subscription = firestore.collection(USERS_COLLECTION).document(chatUserId)
                        .addSnapshotListener { user, _ ->
                            if (user != null) {
                                channel.offer(
                                    RecentChat(
                                        user.get(NAME_FIELD) as String,
                                        chatUserId,
                                        user.getTimestamp(LAST_ONLINE_FIELD)?.toDate() ?: Date(),
                                        user.get(IS_ONLINE_FIELD) as Boolean,
                                        chat.id,
                                    )
                                )
                            } else {
                                channel.offer(RecentChat.getDefault())
                            }
                        }
                    awaitClose {
                        subscription.remove()
                    }
                }
            }
            // combine all the flows
            combine(flowList) {
                it.toList()
            }
        }
    }


    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val USERS_FIELD = "users"
        private const val USERS_COLLECTION = "users"
        private const val NAME_FIELD = "name"
        private const val IS_ONLINE_FIELD = "isOnline"
        private const val LAST_ONLINE_FIELD = "lastOnline"
    }
}