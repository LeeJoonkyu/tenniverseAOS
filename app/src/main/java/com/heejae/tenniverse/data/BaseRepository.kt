package com.heejae.tenniverse.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseRepository {

    protected val db = Firebase.firestore
    protected val users = db.collection("users")
    protected val rents = db.collection("rents")
    protected val versionRef = db.collection("version")
    protected val scope = CoroutineScope(Dispatchers.IO)

}