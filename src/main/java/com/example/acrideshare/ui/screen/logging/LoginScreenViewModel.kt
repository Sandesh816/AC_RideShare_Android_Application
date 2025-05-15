package com.example.acrideshare.ui.screen.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginEvent {
    object Success : LoginEvent
    data class Error(val msg: String) : LoginEvent
}

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val repo: AuthRepo,
) : ViewModel() {

    data class UiState(
        val email:String="",
        val password:String="",
        val loading:Boolean=false,
        val error:String?=null
    ){
        val canLogin get() = email.isNotBlank() && password.length>=6 && !loading
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(v:String)   { _ui.update { it.copy(email=v) } }
    fun onPwdChanged(v:String)     { _ui.update { it.copy(password=v) } }

    fun login() = viewModelScope.launch(Dispatchers.IO) {
        _ui.update { it.copy(loading = true, error=null) }
        runCatching { repo.signIn(_ui.value.email,_ui.value.password) }
            .onSuccess { _events.send(LoginEvent.Success)}
            .onFailure { _events.send(
                LoginEvent.Error(
                    it.localizedMessage ?: "Invalid credentials"
                )
            )}
        _ui.update { it.copy(loading = false) }
    }
}