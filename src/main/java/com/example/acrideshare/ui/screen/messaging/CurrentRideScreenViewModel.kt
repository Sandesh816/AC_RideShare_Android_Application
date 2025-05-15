package com.example.acrideshare.ui.screen.messaging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.data.repo.MessageRepo
import com.example.acrideshare.data.repo.RideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentRideScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val rideRepo: RideRepo,
    private val msgRepo: MessageRepo,
    private val authRepo: AuthRepo,
) : ViewModel() {

    val uid = authRepo.currentUser?.uid ?: ""

    private val rideId: String =
        savedStateHandle["rideId"]
            ?: throw IllegalArgumentException("rideId missing")

    val ride = rideRepo.observeRide(rideId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val messages = msgRepo.observeMessages(rideId)
        .onEach { list ->
            list.filter { uid !in it.readBy }
                .forEach { msgRepo.markRead(rideId, it.id, uid) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun send(text: String) = viewModelScope.launch(Dispatchers.IO) {
        if (text.isNotBlank()) msgRepo.sendMessage(rideId, text, uid)
    }
}

//@HiltViewModel
//class CurrentRideScreenViewModel @Inject constructor(
//    savedStateHandle: SavedStateHandle,
//    private val repo: RideRepo,
////    private val msgRepo: MessageRepo,
//) : ViewModel() {
//
//    sealed class RideState {
//        object Idle : RideState() // initial condition
//        object Loading : RideState()
//        data class Success(val ride: Ride) : RideState()
//        object Error : RideState()
//    }
//
//    private val _rideState = MutableStateFlow<RideState>(RideState.Idle)
//    val rideState: StateFlow<RideState> = _rideState
//    val rideID: String? = savedStateHandle["rideId"]
//
//    init{
//        rideID?.let { id ->
//            getRide(id)
//        } ?: run {
//            _rideState.value = RideState.Error
//        }
//    }
//
//     fun getRide(rideID: String){
//         viewModelScope.launch {
//             _rideState.value = RideState.Loading
//             try{
//                 val ride = repo.getRide(rideID)
//
//                 if (ride != null) _rideState.value = RideState.Success(ride)
//                 else _rideState.value = RideState.Error
//
//             } catch (e: Exception){
//                 _rideState.value = RideState.Error
//             }
//         }
//    }
//}

//    val ride = repo.observeRide(rideId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
//    val messages = msgRepo.observeMessages(rideId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
//
//    fun send(text: String) = viewModelScope.launch(dispatcher) {
//        msgRepo.sendMessage(rideId, text)
//    }