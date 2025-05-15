package com.example.acrideshare.ui.screen.ride

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.Ride
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.data.repo.RideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SearchRidesScreenViewModel @Inject constructor(
    private val repo: RideRepo,
    private val authRepo: AuthRepo,
) : ViewModel() {

    private val userCache = mutableMapOf<String, String>()      // uid → name

    suspend fun displayName(uid: String): String {
        return userCache[uid] ?: run {
            val snap = repo.returnUser(uid) ?: return uid.take(6)
            val name = snap.getString("name") ?: uid.take(6)
            userCache[uid] = name
            name
        }
    }

    data class Filter(val origin: String? = null, val destination: String? = null, val date: Date? = null)
    private val filter = MutableStateFlow(Filter())
    private val allRides = repo.getUpcomingRides()


    sealed class RideActionState{
        object Idle: RideActionState()
        object Loading: RideActionState()
        data class Success(val message: String): RideActionState()
        data class Error(val message: String): RideActionState()
    }

    private val _rideActionState = MutableStateFlow<RideActionState>(RideActionState.Idle)
    val rideActionState: StateFlow<RideActionState> = _rideActionState

    val uiRides: StateFlow<List<Ride>> = combine(allRides, filter) { list, f ->
        list.filter { r ->
            (f.origin.isNullOrBlank()      || r.originCity.startsWith(f.origin, true)) &&
                    (f.destination.isNullOrBlank() || r.destinationCity.startsWith(f.destination, true)) &&
                    (f.date == null               || sameDay(r.startDate, f.date))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onOrigin(text: String)  { filter.update { it.copy(origin = text) } }
    fun onDestination(text: String) { filter.update { it.copy(destination = text) } }
    fun onDate(d: Date) {filter.update { it.copy(date = d) }}

    val uid get() = authRepo.currentUser?.uid

    fun canJoin(ride: Ride): Boolean = uid != null && uid != ride.creatorID && uid !in ride.passengersList && ride.passengersList.size < ride.seatsAvailable
    fun requestJoin(ride: Ride){
        if(ride == null || uid == null || !canJoin(ride)) return
        viewModelScope.launch {
            _rideActionState.value = RideActionState.Loading
            try{
                repo.joinRide(ride.rideID, uid!!)
                _rideActionState.value = RideActionState.Success("Successfully joined ride")
            }catch (e: Exception){
                _rideActionState.value = RideActionState.Error("Failed to join ride: ${e.message}")
            }
        }
    }

    fun deleteRide(ride: Ride) = viewModelScope.launch(Dispatchers.IO) {
        if (uid == ride.creatorID) {
            _rideActionState.value = RideActionState.Loading
            try {
                Log.d("ViewModel", "Calling repo.deleteRide for: ${ride.rideID}")
                repo.deleteRide(ride.rideID)
                Log.d("ViewModel", "repo.deleteRide successful")
                _rideActionState.value = RideActionState.Success("Ride deleted successfully!")
            } catch (e: Exception) {
                _rideActionState.value =
                    RideActionState.Error("Failed to delete ride: ${e.message}")
            }
        } else {
            _rideActionState.value = RideActionState.Error("You are not the creator of this ride.")
        }
    }

    fun leaveRide(ride: Ride){
        if(ride == null || uid == null) return
        viewModelScope.launch {
            _rideActionState.value = RideActionState.Loading
            try {
                repo.leaveRide(ride.rideID, uid!!)
                _rideActionState.value = RideActionState.Success("Successfully left ride!")
            } catch (e: Exception) {
                _rideActionState.value = RideActionState.Error("Failed to leave ride: ${e.message}")
            }
        }
    }

    fun isMember(ride: Ride): Boolean = uid == ride.creatorID || uid in ride.passengersList

    fun resetRideActionState() {
        _rideActionState.value = RideActionState.Idle
    }

    private fun sameDay(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance().apply { time = d1 }
        val c2 = Calendar.getInstance().apply { time = d2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}



/* Things to do
1) Create postcard for rides, Add a button at center-right edge: Request to Join (disabled if the ride is full or if the user is the creator). Add a button at top-right corner: delete (visible only if user is the creator). Just below request to join, have a keyboard down arrow, which if clicked, shows the list of passengers
* 2)Postcard is clickable (when clicked, goes to CurrentRidesScreen if the user is a part of the group, else it would be same like clicking on the Request to join button, only possible if the ride is not full again). Probably needs an authRepo as well to check if userID is equal to ride creator ID
* 3) Want to make such that all rides appear sorted by time initially, then as user starts typing, we keep on filtering
* */