package com.silverpants.instantaneous.misc

import com.google.firebase.firestore.FirebaseFirestoreException

class DocumentExistsException :
    FirebaseFirestoreException("Firestore Document exists", Code.ABORTED)