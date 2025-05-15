package com.example.acrideshare.ui.screen.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
fun UpdateNameScreen(nav: NavController, vm: UpdateNameVM = hiltViewModel()) {
    Scaffold(topBar = { WhiteTopBar(" ", nav::popBackStack) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Update your name", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Please enter your name as it appears on your ID", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.name,
                onValueChange = vm::onNameChange,
                label = { Text("Full name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.save { nav.popBackStack() } },
                enabled = vm.name.isNotBlank() && !vm.saving,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}