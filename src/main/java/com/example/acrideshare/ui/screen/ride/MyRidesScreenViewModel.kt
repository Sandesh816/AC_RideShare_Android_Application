package com.example.acrideshare.ui.screen.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.Ride
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.data.repo.RideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRidesViewModel @Inject constructor(
    private val repo: RideRepo,
    private val authRepo: AuthRepo,
) : ViewModel() {
    val currrentUser = authRepo.currentUser
    val uid = currrentUser?.uid

    private val userCache = mutableMapOf<String, String>()      // uid → name

    suspend fun displayName(uid: String): String {
        return userCache[uid] ?: run {
            val snap = repo.returnUser(uid) ?: return uid.take(6)
            val name = snap.getString("name") ?: uid.take(6)
            userCache[uid] = name
            name
        }
    }

    val myUpcomingRides: StateFlow<List<Ride>> = repo.getUpcomingRides().map { rides ->
        rides.filter { ride ->
            ride.creatorID == uid ||
                    uid in ride.passengersList
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val myPastRides: StateFlow<List<Ride>> = repo.getPastRides().map { rides ->
        rides.filter { ride ->
            ride.creatorID == uid ||
                    uid in ride.passengersList
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun deleteRide(ride: Ride) = viewModelScope.launch(Dispatchers.IO) {
        if (uid == ride.creatorID) {
            try {
                repo.deleteRide(ride.rideID)
            } catch (e: Exception) {}
        }
    }

    fun leaveRide(ride: Ride){
        if(ride == null || uid == null) return
        viewModelScope.launch {
            try {
                repo.leaveRide(ride.rideID, uid!!)
            } catch (e: Exception) {}
        }
    }
}