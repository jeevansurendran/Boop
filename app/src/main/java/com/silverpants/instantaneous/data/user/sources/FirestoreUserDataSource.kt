package com.silverpants.instantaneous.data.user.sources

import com.google.firebase.firestore.FirebaseFirestore
import com.silverpants.instantaneous.data.user.DocumentExistsException
import com.silverpants.instantaneous.data.user.models.FirestoreUserInfo
import com.silverpants.instantaneous.misc.suspendAndWait
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val UID_KEY = "uid"
    }

    @ExperimentalCoroutinesApi
    suspend fun isFirestoreUserDataExists(uid: String?): Boolean {
        if (uid.isNullOrEmpty()) {
            return false
        }
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .whereEqualTo(UID_KEY, uid)
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

}