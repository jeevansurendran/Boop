package com.silverpants.instantaneous.data.chat.sources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.chat.model.RecentChat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class RecentChatDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    fun getObservableRecentChat(userId: String): Flow<List<RecentChat>> {
        // User id is empty hence return
        if (userId.isEmpty()) {
            flow {
                emit(emptyList<RecentChat>())
            }
        }
        val chatsFlow = channelFlow<List<DocumentSnapshot>> {
            val chatCollection = firestore
                .collection(CHATS_COLLECTION)
                .whereArrayContains(USERS_FIELD, userId)

            val subscription = chatCollection.addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    channel.offer(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot.isEmpty) {
                    channel.offer(emptyList())
                    return@addSnapshotListener
                }

                channel.offer(snapshot.documents)
            }
            awaitClose { subscription.remove() }
        }
        return chatsFlow.flatMapLatest { chatList ->
            // create a list of flow<RecentChat>
            if(chatList.isEmpty()) {
                return@flatMapLatest flow { emit(emptyList<RecentChat>()) }
            }
            val flowList = chatList.map { chat ->
                val list: List<String> = chat[USERS_FIELD] as List<String>
                val chatUserId = if (list[0] == userId) list[1] else list[0]
                channelFlow<RecentChat> {
                    val subscription = firestore.collection(USERS_COLLECTION).document(chatUserId)
                        .addSnapshotListener { user, _ ->
                            if (user != null) {
                                channel.offer(
                                    RecentChat(
                                        user[NAME_FIELD] as String,
                                        chatUserId,
                                        user.getTimestamp(LAST_ONLINE_FIELD)?.toDate() ?: Date(),
                                        user[IS_ONLINE_FIELD] as Boolean,
                                        chat.id,
                                        user[PHOTO_URL_FIELD] as String,
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
        private const val PHOTO_URL_FIELD = "photoURL"
    }
}