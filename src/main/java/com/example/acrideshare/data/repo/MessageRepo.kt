package com.example.acrideshare.data.repo

import com.example.acrideshare.data.Message
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepo @Inject constructor(private val db: FirebaseFirestore) {

    private fun msgCol(rideId: String) =
        db.collection("rides").document(rideId).collection("messages")

    fun observeMessages(rideId: String): Flow<List<Message>> = callbackFlow {
        val reg = msgCol(rideId)
            .orderBy("sentAt")
            .addSnapshotListener { snap, e ->
                if (e != null) close(e)
                else trySend(snap?.toObjects(Message::class.java) ?: emptyList())
            }
        awaitClose { reg.remove() }
    }

    suspend fun sendMessage(rideId: String, text: String, uid: String) {
        val ref = msgCol(rideId).document()
        val msg = Message(id = ref.id, senderId = uid, text = text)
        ref.set(msg).await()
    }

    suspend fun markRead(rideId: String, msgId: String, uid: String) {
        msgCol(rideId).document(msgId)
            .update("readBy", FieldValue.arrayUnion(uid))
    }
}