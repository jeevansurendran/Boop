package com.silverpants.instantaneous.data.chat.sources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.chat.model.Chat
import com.silverpants.instantaneous.data.chat.model.Message
import com.silverpants.instantaneous.data.chat.model.Messages
import com.silverpants.instantaneous.data.chat.model.Messages.MessageChange
import com.silverpants.instantaneous.misc.CHAT_MAX_DISPLAY_MESSAGES
import com.silverpants.instantaneous.misc.DocumentNotFoundException
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.*
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
                    chatRoom.sUserId = userId
                    chatRoom.chatId = chatId
                    channel.offer(chatRoom)
                }
            }
            awaitClose {
                subscription.remove()
            }
        }
    }

    fun getObservableChatMessages(chatId: String, userId: String): Flow<Messages> {
        return channelFlow {
            val messagesQuery = firestore
                .collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_COLLECTION)
                .orderBy(TIMESTAMP_FIELD)
                .limit(CHAT_MAX_DISPLAY_MESSAGES)
            val registration = messagesQuery.addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    channel.offer(Messages())
                    return@addSnapshotListener
                }
                if (snapshot.isEmpty) {
                    channel.offer(Messages())
                    return@addSnapshotListener
                }
                val chats = snapshot.documents.map {
                    parseMessage(snapshot = it, userId = userId)
                }
                val documentChanges = snapshot.documentChanges.map {
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = parseMessage(it.document, userId)
                            MessageChange(
                                MessageChange.Type.ADDED,
                                message,
                                it.oldIndex,
                                it.newIndex
                            )
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val message = parseMessage(it.document, userId)
                            MessageChange(
                                MessageChange.Type.MODIFIED,
                                message,
                                it.oldIndex,
                                it.newIndex
                            )
                        }
                        DocumentChange.Type.REMOVED -> {
                            val message = parseMessage(it.document, userId)
                            MessageChange(
                                MessageChange.Type.REMOVED,
                                message,
                                it.oldIndex,
                                it.newIndex
                            )
                        }
                    }
                }
                channel.offer(Messages(chats, documentChanges))
            }
            awaitClose {
                registration.remove()
            }
        }
    }

    suspend fun postSendersImmediateMessage(chatId: String, message: String, index: Int) {
        val chatDocument = firestore.collection(CHATS_COLLECTION).document(chatId)
        val immediateField = if (index == 0) IMMEDIATE_1_FIELD else IMMEDIATE_2_FIELD
        chatDocument.update(immediateField, message).suspendAndWait()
    }

    suspend fun postSendersNewMessage(chatId: String, message: String, userId: String) {
        val messagesCollection = firestore
            .collection(CHATS_COLLECTION)
            .document(chatId)
            .collection(MESSAGES_COLLECTION)

        messagesCollection.document().set(
            hashMapOf(
                MESSAGE_FIELD to message,
                TIMESTAMP_FIELD to Timestamp(Calendar.getInstance().time),
                USER_ID_FIELD to userId
            )
        ).suspendAndWait()
    }

//    suspend fun getChatId(user1: String, user2: String): String {
//
//    }

    private fun parseMessage(snapshot: DocumentSnapshot, userId: String): Message {
        return snapshot.toObject(Message::class.java)!!.apply {
            isMe = snapshot[USER_ID_FIELD] == userId
            messageId = snapshot.id
        }

    }

    companion object {
        private const val CHATS_COLLECTION = "chats"
        private const val MESSAGES_COLLECTION = "messages"
        private const val TIMESTAMP_FIELD = "timestamp"
        private const val USER_ID_FIELD = "userId"
        private const val IMMEDIATE_1_FIELD = "immediate1"
        private const val IMMEDIATE_2_FIELD = "immediate2"
        private const val MESSAGE_FIELD = "message"
    }
}