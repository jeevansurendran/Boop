package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.user.DocumentExistsException
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.data.user.models.User
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseDataSource: FirebaseUserDataSource
) {
    @ExperimentalCoroutinesApi
    suspend fun isFirestoreUserDataExists(uid: String?): Boolean {
        if (uid.isNullOrEmpty()) {
            return false
        }
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .whereEqualTo(UID_FIELD, uid)
            .get()
            .suspendAndWait()

        return !snapshot.isEmpty && snapshot.documents[0] != null && !(snapshot.documents[0].get(
            "uid"
        ) as? String).isNullOrEmpty()
    }

    @ExperimentalCoroutinesApi
    suspend fun postUserIdAndNumber(userId: String, uid: String, number: String): FirestoreUserInfo {
        val userDoc = firestore.collection(USERS_COLLECTION).document(userId)

        val snapshot = userDoc.get().suspendAndWait()
        if (snapshot.exists()) {
            throw DocumentExistsException()
        }
        val data = FirestoreUserInfo(uid, number)
        userDoc.set(data).suspendAndWait()
        return data
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