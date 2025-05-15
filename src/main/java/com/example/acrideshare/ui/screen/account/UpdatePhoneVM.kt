package com.example.acrideshare.ui.screen.account

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class UpdatePhoneVM @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    var phone    by mutableStateOf("")
    var code     by mutableStateOf("")
    var stage    by mutableStateOf(0)   // 0 = input, 1 = verify
    var loading  by mutableStateOf(false)
    private var verId: String? = null

    fun sendCode(activity: Activity) {
        if (phone.length < 10) return
        loading = true
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(cred: PhoneAuthCredential) {}
                    override fun onVerificationFailed(e: FirebaseException) { loading = false }
                    override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verId = id; loading = false; stage = 1
                    }
                })
                .build()
        )
    }

    fun verify() = viewModelScope.launch {
        val id = verId ?: return@launch
        loading = true
        val cred = PhoneAuthProvider.getCredential(id, code)
        auth.currentUser!!.updatePhoneNumber(cred).await()

        db.collection("users").document(auth.currentUser!!.uid)
            .update("phone", phone).await()

        loading = false; stage = 2      // done
    }
}