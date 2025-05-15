package com.example.acrideshare.ui.screen.logging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavController, vm: LoginScreenViewModel = hiltViewModel()) {
    val uiState by vm.ui.collectAsState()
    var showWelcomeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                LoginEvent.Success -> nav.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }

                is LoginEvent.Error -> vm.onEmailChanged("")
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = CenterHorizontally
            ) {
                TopAppBar(
                    title = { Text("Welcome to AC RideShare") },
                    actions = {
                        IconButton(onClick = {
                            showWelcomeDialog = true
                        }) {
                            Icon(Icons.Filled.Info, contentDescription = "Info")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                if (showWelcomeDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showWelcomeDialog = false
                        },
                        title = {
                            Text(text = "Welcome!")
                        },
                        text = {
                            Text(text = "This is a short welcome message.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showWelcomeDialog = false
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
                OutlinedTextField(uiState.email, vm::onEmailChanged, label = { Text("Email") })
                OutlinedTextField(
                    uiState.password, vm::onPwdChanged, label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation('*')
                )
                Button(onClick = vm::login, enabled = uiState.canLogin) { Text("Login") }
                TextButton(onClick = { nav.navigate(Screen.SignUp.route) }) { Text("New user? Sign Up") }
                TextButton(onClick = { nav.navigate(Screen.ForgotPwd.route) }) { Text("Forgot password?") }
                uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}