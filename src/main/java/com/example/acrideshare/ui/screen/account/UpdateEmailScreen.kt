package com.example.acrideshare.ui.screen.account

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.screen.WhiteTopBar

@Composable
fun UpdateEmailScreen(nav: NavController, vm: UpdateEmailVM = hiltViewModel()) {
    Scaffold(topBar = { WhiteTopBar(" ", nav::popBackStack) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Update your email", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("We’ll send you a verification link.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = vm.email,
                onValueChange = { vm.email = it },
                label = { Text("New email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.sendLink { nav.popBackStack() } },
                enabled = Patterns.EMAIL_ADDRESS.matcher(vm.email).matches() && !vm.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Continue") }
        }
    }
}