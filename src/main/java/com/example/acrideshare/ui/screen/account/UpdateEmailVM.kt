package com.example.acrideshare.ui.screen.account

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UpdateEmailVM @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    var email   by mutableStateOf("")
    var loading by mutableStateOf(false)

    fun sendLink(onDone: () -> Unit) = viewModelScope.launch {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return@launch
        loading = true
        auth.currentUser!!.verifyBeforeUpdateEmail(email).await()
        db.collection("users").document(auth.currentUser!!.uid)
            .update("email", email).await()
        loading = false
        onDone()
    }
}