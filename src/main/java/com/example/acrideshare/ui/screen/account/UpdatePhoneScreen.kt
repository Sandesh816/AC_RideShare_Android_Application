package com.example.acrideshare.ui.screen.account

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.screen.WhiteTopBar

@Composable
fun UpdatePhoneScreen(nav: NavController, vm: UpdatePhoneVM = hiltViewModel()) {
    Scaffold(topBar = { WhiteTopBar(" ", nav::popBackStack) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Update your phone number", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("We’ll send you a code for verification.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))

            when (vm.stage) {
                0 -> {
                    OutlinedTextField(
                        value = vm.phone,
                        onValueChange = { vm.phone = it },
                        label = { Text("New phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
//                            LocalContext.current
//                            .findActivity()
//                            ?.let { vm.sendCode(it) }
                                  },
                        enabled = vm.phone.length >= 10 && !vm.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Continue") }
                }
                1 -> {
                    OutlinedTextField(
                        value = vm.code,
                        onValueChange = { vm.code = it },
                        label = { Text("Verification code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { vm.verify(); nav.popBackStack() },
                        enabled = vm.code.length >= 6 && !vm.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Verify & Save") }
                }
                2 -> {
                    Text("✅ Phone updated!", color = Color.Green)
                }
            }
        }
    }
}
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}