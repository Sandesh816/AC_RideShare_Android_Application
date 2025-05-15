package com.example.acrideshare.ui.screen.logging

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.repo.AuthRepo
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


sealed interface SignUpEvent {
    object Success : SignUpEvent
    data class Error(val msg: String) : SignUpEvent
}

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val repo: AuthRepo,
) : ViewModel() {
    data class UiState(
        val name: String = "",
        val phone: String = "",
        val email: String = "",
        val pwd: String = "",
        val confirm: String = "",
        val loading: Boolean = false,
        val error: String? = null
    ) {
        fun isValidEmail(e: String) =
            Patterns.EMAIL_ADDRESS.matcher(e).matches()

        fun isValidPhone(p: String) =
            Patterns.PHONE.matcher(p).matches() && p.length >= 10

        val canCreate get() =
            name.isNotBlank() &&
                    phone.isNotBlank() &&
                    email.isNotBlank() &&
                    pwd == confirm &&
                    isValidEmail(email)&&
                    isValidPhone(phone)&&
                    pwd.length >= 6 &&
                    !loading
    }


    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun onName(v:String)=_ui.update{it.copy(name=v)}
    fun onPhone(v:String)=_ui.update{it.copy(phone=v)}
    fun onEmail(v:String)=_ui.update{it.copy(email=v)}
    fun onPwd(v:String)=_ui.update{it.copy(pwd=v)}
    fun onConfirm(v:String)=_ui.update{it.copy(confirm=v)}

    private val _events = Channel<SignUpEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun createUser() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        val u = ui.value

        runCatching {
            Log.e("SignUpVM", "opening")
            val authRes = repo.signUp(u.email, u.pwd)
            Log.e("SignUpVM", "signup done")
            val uid = authRes.user!!.uid

            authRes.user!!.updateProfile(
                userProfileChangeRequest { displayName = u.name }
            ).await()
            Log.e("SignUpVM", "profile updated")

            repo.createUserDoc(uid, u.name, u.email, u.phone)
        }.onSuccess { _events.send(SignUpEvent.Success) }
        .onFailure { _events.send(SignUpEvent.Error(it.localizedMessage ?: "error")) }
    _ui.update { it.copy(loading = false) }
    }
}