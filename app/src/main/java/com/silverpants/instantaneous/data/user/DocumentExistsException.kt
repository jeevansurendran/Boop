package com.silverpants.instantaneous.data.user

import com.google.firebase.firestore.FirebaseFirestoreException

class DocumentExistsException :
    FirebaseFirestoreException("Firestore Document exists", Code.ABORTED)