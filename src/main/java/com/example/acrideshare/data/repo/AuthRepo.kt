package com.example.acrideshare.data.repo

import com.example.acrideshare.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepo @Inject constructor( val auth: FirebaseAuth){
    val currentUser get() = auth.currentUser
    suspend fun signIn(email: String, password: String) = auth.signInWithEmailAndPassword(email, password).await()
    suspend fun signUp(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password).await()
    suspend fun createUserDoc(uid: String, name: String, email: String, phone: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(User(uid = uid, name = name, email = email, phone = phone))
            .await()
    }
    suspend fun sendReset(email: String) = auth.sendPasswordResetEmail(email).await()
    fun signOut() = auth.signOut()
}
// Removed .await() because it was not working