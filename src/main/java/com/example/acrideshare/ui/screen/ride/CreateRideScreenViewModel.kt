package com.example.acrideshare.ui.screen.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.Ride
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.data.repo.RideRepo
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

val CityOptions = listOf("Amherst College", "Bradley Airport" , "New York City", "Boston", "Washington DC")

@HiltViewModel
class CreateRideScreenViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val repo: RideRepo,
) : ViewModel() {
    data class UiState(
        val origin: String = "",
        val destination: String = "",
        val seats: Int = 1,
        val time: Timestamp = Timestamp.now(),
        val date: Date = Date(),
        val loading: Boolean = false,
        val error: String? = null,
        val created: Boolean = false
    ) {
        val canCreate get() = origin.isNotBlank() && destination.isNotBlank()  && !loading && (date > Date() || time > Timestamp.now())
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun onOrigin(v: String) = _ui.update { it.copy(origin = v) }
    fun onDestination(v: String) = _ui.update { it.copy(destination = v) }
    fun incSeats() = _ui.update { it.copy(seats = it.seats + 1) }
    fun decSeats() = _ui.update { it.copy(seats = maxOf(1, it.seats - 1)) }
    fun onDate(d: Date) = _ui.update { it.copy(date = d) }
    fun onTime(t: Timestamp) = _ui.update { it.copy(time = t) }

    val uid = authRepo.currentUser!!.uid

    suspend fun getUserName(uid: String): String{
        val snap = repo.returnUser(uid) ?: return uid.take(6)
        return snap.getString("name") ?: uid.take(6)
    }

    fun createRide() = viewModelScope.launch(Dispatchers.IO) {
        val user = authRepo.currentUser ?: return@launch
        _ui.update { it.copy(loading = true, error = null) }
        val ride = Ride(
            // rideID auto generation in repo
            creatorID = user.uid,
            originCity = _ui.value.origin,
            destinationCity = _ui.value.destination,
            seatsAvailable = _ui.value.seats,
            startDate = _ui.value.date,
            startTime = _ui.value.time,
            passengersList = emptyList(),
            creatorName = getUserName(uid)
        )
        runCatching { repo.createRide(ride) }
            .onSuccess { _ui.update { it.copy(created = true) } }
            .onFailure { _ui.update { it.copy(error = "Error") } }
        _ui.update { it.copy(loading = false) }
    }
}

/*
* Update: creator does not need to be in the passengers list
* We are only checking time > current time but should check date as well (Done)
* */
