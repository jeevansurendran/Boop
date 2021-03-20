package com.silverpants.instantaneous.data.user.models

import com.google.firebase.auth.FirebaseUser
import java.util.*

data class User(
    val firebaseUser: FirebaseUser?,
    val name: String,
    val userId: String,
    val number: String,
    val isOnline: Boolean,
    val lastOnline: Date,
    val photoURL: String,
) : FirebaseUserInfo(firebaseUser)