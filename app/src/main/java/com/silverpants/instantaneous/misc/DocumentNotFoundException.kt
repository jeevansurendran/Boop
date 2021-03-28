package com.silverpants.instantaneous.misc

import com.google.firebase.firestore.FirebaseFirestoreException

class DocumentNotFoundException :
    FirebaseFirestoreException("Firestore Document does not exist", Code.ABORTED)