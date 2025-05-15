package com.example.acrideshare.ui.screen.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UpdateNameVM @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set

    fun onNameChange(v: String) { name = v }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        if (name.isBlank()) return@launch
        saving = true
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).update("name", name).await()
        auth.currentUser!!.updateProfile(
            userProfileChangeRequest { displayName = name }
        ).await()
        saving = false
        onDone()
    }
}