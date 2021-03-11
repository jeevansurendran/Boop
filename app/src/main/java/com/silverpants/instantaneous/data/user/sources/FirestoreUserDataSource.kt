package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.silverpants.instantaneous.data.user.DocumentExistsException
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.misc.Result
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject

class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val REGISTERED_KEY = "registered"
    }

    @ExperimentalCoroutinesApi
    fun observeUserChanges(uid: String): Flow<Result<Boolean?>> {
        return channelFlow<Result<Boolean?>> {
            // Watch the document
            val registeredChangedListener =
                { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                    if (snapshot == null || !snapshot.exists()) {
                        // When the account signs in for the first time, the document doesn't exist
                        Timber.d("Document for snapshot $uid doesn't exist")
                        channel.offer(Result.Success(false))
                    } else {
                        val isRegistered: Boolean? = snapshot.get(REGISTERED_KEY) as? Boolean
                        Timber.d("Received registered flag: $isRegistered")
                        channel.offer(Result.Success(isRegistered))
                    }
                    Unit // Avoids returning the Boolean from channel.offer
                }

            val registeredChangedListenerSubscription = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .addSnapshotListener(registeredChangedListener)

            awaitClose { registeredChangedListenerSubscription.remove() }
        }
            // Only emit a value if it's a new value or a value change.
            .distinctUntilChanged()
    }

    @ExperimentalCoroutinesApi
    suspend fun postUserId(userId: String, uid: String): FirestoreUserInfo {
        val userDoc = firestore.collection(USERS_COLLECTION).document(userId)

        val snapshot = userDoc.get().suspendAndWait()
        if (snapshot.exists()) {
            throw DocumentExistsException()
        }
        val data = FirestoreUserInfo(uid)
        userDoc.set(data).suspendAndWait()
        return data
    }

}