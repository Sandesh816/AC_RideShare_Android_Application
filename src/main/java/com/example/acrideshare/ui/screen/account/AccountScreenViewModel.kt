package com.example.acrideshare.ui.screen.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.User
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.data.repo.RideRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    private val repo: AuthRepo,
    private val rideRepo: RideRepo
) : ViewModel() {

    private val uid = repo.currentUser?.uid ?: ""

    private val _profile = MutableStateFlow<User?>(null)
    val  profile: StateFlow<User?> = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            if (uid.isNotBlank()) {
                val snap = rideRepo.returnUser(uid)
                _profile.value = snap?.toObject(User::class.java)
                Log.d("VM", "Fetched user: ${uid}")
                Log.d("VM", "Fetched user: ${_profile.value}")
            } else {
                Log.w("VM", "No UID – user not logged in")
            }
        }
    }

    fun logout() = repo.signOut()
}