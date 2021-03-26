package com.silverpants.instantaneous.data.chat.sources

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.silverpants.instantaneous.data.chat.model.Chat
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.misc.CHAT_MAX_DISPLAY_MESSAGES
import com.silverpants.instantaneous.misc.DocumentNotFoundException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class ChatDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    fun getObservableChat(chatId: String, userId: String): Flow<Chat> {
        return channelFlow {
            val chatDocument = firestore
                .collection(CHATS_COLLECTION)
                .document(chatId)

            val subscription = chatDocument.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.exists()) {
                    throw DocumentNotFoundException()
                }
                val chatRoom = snapshot?.toObject(Chat::class.java)
                if (chatRoom != null) {
                    chatRoom.meUserId = userId
                    chatRoom.chatId = chatId
                    channel.offer(chatRoom)
                }
            }
            awaitClose {
                subscription.remove()
            }
        }
    }

    fun getObservableChatMessages(chatId: String, userId: String): Flow<List<Message>> {
        return channelFlow {
            val messagesQuery = firestore
                .collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_COLLECTION)
                .orderBy(TIMESTAMP_FIELD, Query.Direction.DESCENDING)
                .limit(CHAT_MAX_DISPLAY_MESSAGES)
            val registration = messagesQuery.addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    channel.offer(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot.isEmpty) {
                    channel.offer(emptyList())
                    return@addSnapshotListener
                }
                val chats = snapshot.documents.map {
                    it.toObject(Message::class.java)!!
                }
                channel.offer(chats)
            }
            awaitClose {
                registration.remove()
            }
        }
    }

    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
        private const val TIMESTAMP_FIELD = "messages"
    }
}