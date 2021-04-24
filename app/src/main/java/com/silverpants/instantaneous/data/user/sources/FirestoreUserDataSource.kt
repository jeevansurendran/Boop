package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.user.models.AnotherUser
import com.silverpants.instantaneous.data.user.models.User
import com.silverpants.instantaneous.data.user.models.UserState
import com.silverpants.instantaneous.misc.DocumentExistsException
import com.silverpants.instantaneous.misc.FALLBACK_DPS
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseDataSource: FirebaseUserDataSource
) {
    suspend fun isUserDataExists(uid: String?): UserState {
        if (uid.isNullOrEmpty()) {
            return UserState.NOT_EXIST
        }
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .whereEqualTo(UID_FIELD, uid)
            .get()
            .suspendAndWait()

        return if (!snapshot.isEmpty && snapshot.documents[0] != null && (snapshot.documents[0].get(
                UID_FIELD
            ) as? String == uid)
        ) UserState.EXISTS else UserState.NO_DATA
    }

    suspend fun createUser(
        userId: String,
        uid: String,
        number: String,
        name: String
    ) {
        val userDoc = firestore.collection(USERS_COLLECTION).document(userId)

        val snapshot = userDoc.get().suspendAndWait()
        if (snapshot.exists()) {
            throw DocumentExistsException()
        }
        val data = hashMapOf(
            IS_ONLINE_FIELD to true,
            LAST_ONLINE_FIELD to Calendar.getInstance().time,
            NAME_FIELD to name,
            PHOTO_URL_FIELD to FALLBACK_DPS.random(),
            UID_FIELD to uid,
            NUMBER_FIELD to number
        )
        userDoc.set(data).suspendAndWait()
    }

    fun getUserInfo(): Flow<User> {
        return firebaseDataSource.getObservableFirebaseUser().map {
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .whereEqualTo(UID_FIELD, it?.uid)
                .get()
                .suspendAndWait()
            if (snapshot.isEmpty) {
                throw UnsupportedOperationException()
            }

            val firestoreUserInfo = snapshot.documents[0].data!!
            User(
                it,
                firestoreUserInfo[NAME_FIELD] as String,
                snapshot.documents[0].id,
                firestoreUserInfo[NUMBER_FIELD] as String,
                firestoreUserInfo[IS_ONLINE_FIELD] as Boolean,
                snapshot.documents[0].getTimestamp(LAST_ONLINE_FIELD)?.toDate() ?: Date(),
                firestoreUserInfo[PHOTO_URL_FIELD] as String
            )
        }
    }

    fun getAnotherUserInfo(userId: String): Flow<AnotherUser> {
        return channelFlow {
            val anotherUserDocument = firestore
                .collection(USERS_COLLECTION)
                .document(userId)

            val subscription = anotherUserDocument.addSnapshotListener { snapshot, _ ->
                if (snapshot == null || snapshot.data == null) {
                    return@addSnapshotListener
                }

                val firestoreUserInfo = snapshot.data!!
                channel.offer(
                    AnotherUser(
                        firestoreUserInfo[NAME_FIELD] as String,
                        snapshot.id,
                        firestoreUserInfo[IS_ONLINE_FIELD] as Boolean,
                        snapshot.getTimestamp(LAST_ONLINE_FIELD)?.toDate() ?: Date(),
                        firestoreUserInfo[PHOTO_URL_FIELD] as String
                    )
                )
            }
            awaitClose {
                subscription.remove()
            }

        }
    }

    fun searchUsers(query: Flow<String>): Flow<List<AnotherUser>> {
        val anotherUserCollection = firestore
            .collection(USERS_COLLECTION)
        return query.map { query ->
            if (query.length < 3) {
                return@map emptyList()
            }
            return@map try {
                val anotherUser = anotherUserCollection
                    .document(query)
                    .get()
                    .suspendAndWait()
                    .toObject(AnotherUser::class.java)!!
                anotherUser.userId = query
                listOf(anotherUser)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val UID_FIELD = "uid"
        private const val NAME_FIELD = "name"
        private const val NUMBER_FIELD = "number"
        private const val IS_ONLINE_FIELD = "isOnline"
        private const val LAST_ONLINE_FIELD = "lastOnline"
        private const val PHOTO_URL_FIELD = "photoURL"
    }
}