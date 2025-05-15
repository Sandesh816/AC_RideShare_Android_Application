package com.example.acrideshare.ui.screen.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acrideshare.data.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForgotPwdScreenViewModel@Inject constructor(
    private val repo: AuthRepo,
) : ViewModel() {
    data class UiState(
        val email: String = "",
        val sent: Boolean = false,
        val loading: Boolean = false,
        val error: String? = null
    ) {
        val canSend get() = email.isNotBlank() && !loading
    }

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun onEmail(v: String) = _ui.update { it.copy(email = v) }

    fun send() = viewModelScope.launch(Dispatchers.IO) {
        _ui.update { it.copy(loading = true, error = null) }
        runCatching { repo.sendReset(_ui.value.email) }
            .onSuccess { _ui.update { it.copy(sent = true) } }
            .onFailure { _ui.update { it.copy(error = "Error") } }
        _ui.update { it.copy(loading = false, sent = true) }
    }
}
