package com.example.acrideshare.data.repo

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.ui.input.key.type
import com.example.acrideshare.data.Ride
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class RideRepo @Inject constructor(val db: FirebaseFirestore){
    val rides = db.collection("rides")
    suspend fun createRide(ride: Ride) {
        val docRef = rides.document()             // ONE reference
        val id     = docRef.id
        val withId = ride.copy(rideID = id)

        docRef.set(withId).await()                // write to the same doc
    }

    fun observeAll() = callbackFlow<List<Ride>> {
        val reg = rides.orderBy("startTime").addSnapshotListener { snap, e ->
            if (e != null) {
                Log.e("RideRepo", "Error observing rides", e)
                close(e) // Close the flow on error
                return@addSnapshotListener
            }
            val ridesList = snap?.toObjects(Ride::class.java) ?: emptyList()
            Log.d("RideRepo", "Observed ${ridesList.size} rides. Snapshot changes: ${snap?.documentChanges?.size}")
            snap?.documentChanges?.forEach { change ->
                Log.d("RideRepo", "Change type: ${change.type}, Document ID: ${change.document.id}")
            }
            trySend(ridesList)
        }
        awaitClose {
            Log.d("RideRepo", "Stopping ride observation")
            reg.remove()
        }
    }

    suspend fun returnUser(uid: String): DocumentSnapshot? {
        return db.collection("users").document(uid).get().await()
    }

    fun getUpcomingRides(): Flow<List<Ride>> = callbackFlow {
        val now = Timestamp.now()
        val query = rides
            .whereGreaterThan("startTime", now)
            .orderBy("startTime")
        val reg = query.addSnapshotListener { snap, e ->
            if (e != null) {
                Log.e("RideRepo", "observeUpcoming error", e)
                close(e)
                return@addSnapshotListener
            }
            trySend(snap?.toObjects(Ride::class.java) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    fun getPastRides(): Flow<List<Ride>> = callbackFlow {
        val now = Timestamp.now()
        val query = rides
            .whereLessThanOrEqualTo("startTime", now)
            .orderBy("startTime", Query.Direction.DESCENDING)
        val reg = query.addSnapshotListener { snap, e ->
            if (e != null) {
                Log.e("RideRepo", "observePast error", e)
                close(e)
                return@addSnapshotListener
            }
            trySend(snap?.toObjects(Ride::class.java) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    fun observeRide(rideID: String): Flow<Ride?> = callbackFlow {
        val docRef = rides.document(rideID)
        val reg = docRef.addSnapshotListener { snap, e ->
            if (e != null) {
                Log.e("RideRepo", "Error observing ride $rideID", e)
                close(e) // Close the flow on error
                return@addSnapshotListener
            }
            val ride = snap?.toObject(Ride::class.java)
            Log.d("RideRepo", "Observed ride $rideID: $ride")
            trySend(ride)
        }
        awaitClose {
            Log.d("RideRepo", "Stopping ride observation for $rideID")
            reg.remove()
        }
    }


    suspend fun getRide(rideID: String): Ride? {
        return try {
            val documentSnapshot = rides.document(rideID).get().await()
            documentSnapshot.toObject(Ride::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteRide(rideID: String){
        try {
            Log.d("RideRepo", "Attempting to delete ride: $rideID")
            rides.document(rideID).delete().await()
            Log.d("RideRepo", "Ride deleted successfully: $rideID")
        } catch (e: Exception) {
            Log.e("RideRepo", "Error deleting ride: $rideID", e)
            throw e // Re-throw the exception
        }
    }

    suspend fun leaveRide(rideID: String, uid: String?){
        if (uid == null) return

        try {
            db.runTransaction { transaction ->
                val rideRef = rides.document(rideID)
                val snapshot = transaction.get(rideRef)
                val ride = snapshot.toObject(Ride::class.java)

                if (ride != null) {
                    val updatedPassengers = ride.passengersList.toMutableList()
                    updatedPassengers.remove(uid)

                    transaction.update(rideRef, "passengersList", updatedPassengers)
                }
                null
            }.await()
        } catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }
    suspend fun joinRide(rideID: String, uid: String){
        if (uid == null) return
        try {
            db.runTransaction { transaction ->
                val rideRef = rides.document(rideID)
                val snapshot = transaction.get(rideRef)
                val ride = snapshot.toObject(Ride::class.java)

                if (ride != null) {
                    val updatedPassengers = ride.passengersList.toMutableList()
                    updatedPassengers.add(uid)

                    transaction.update(rideRef, "passengersList", updatedPassengers)
                }
                null
            }.await()
        } catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }
}
//    suspend fun deleteRide(rideID: String){
//        try {
//            rides.document(rideID).delete().await()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
//    }

//    fun observeAll() = callbackFlow<List<Ride>> {
//        val reg = rides.orderBy("startTime").addSnapshotListener { snap, _ ->
//            trySend(snap?.toObjects(Ride::class.java) ?: emptyList())
//        }
//        awaitClose { reg.remove() }
//    }


// removed .await()

//    fun search(origin: String?, destination: String?, date: Date?): Query {
//        var q: Query = rides
//        if (!origin.isNullOrBlank())       q = q.whereEqualTo("originCity", origin)
//        if (!destination.isNullOrBlank())  q = q.whereEqualTo("destinationCity", destination)
//        if (date != null)                  q = q.whereEqualTo("startDate", date)
//        return q.orderBy("startTime")
//    }