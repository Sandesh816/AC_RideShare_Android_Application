package com.example.acrideshare.ui.screen.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen

@Composable
fun SignUpScreen(nav: NavController, vm: SignUpScreenViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm.events) {
        vm.events.collect { e ->
            when (e) {
                SignUpEvent.Success -> {
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
                is SignUpEvent.Error -> {
                    snackbarHostState.showSnackbar(e.msg)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                OutlinedTextField(ui.name,  vm::onName,  label = { Text("Full name") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(ui.email, vm::onEmail, label = { Text("Amherst College Email Address")    }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(ui.phone, vm::onPhone, label = { Text("Phone Number")    }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ui.pwd,
                    onValueChange = vm::onPwd,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation('*'),
                    singleLine = true,
                    enabled = !ui.loading,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ui.confirm,
                    onValueChange = vm::onConfirm,
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation('*'),
                    singleLine = true,
                    enabled = !ui.loading,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { vm.createUser() },
                    enabled = ui.canCreate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
                }
            }

            if (ui.loading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }
}