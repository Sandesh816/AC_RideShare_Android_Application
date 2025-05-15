package com.example.acrideshare.ui.screen.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen

@Composable
fun AccountScreen(nav: NavController, vm: AccountScreenViewModel = hiltViewModel()) {
    val profile by vm.profile.collectAsState()
    var showLogout by remember { mutableStateOf(false) }

    /* logout dialog */
    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Log out?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.logout()
                    nav.navigate(Screen.Login.route)
                    { popUpTo(0) }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Back") }
            }
        )
    }

    // body
    val initials = remember(profile?.name) {
        profile?.name
            ?.split(" ")
            ?.take(2)
            ?.joinToString("") { it.first().uppercase() }
            ?: "🙂"
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 32.dp, bottom = 90.dp)   // bottom so FAB doesn’t overlap
    ) {

        /* avatar + name */
        item {
            Surface(
                shape = CircleShape,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .size(96.dp)
                    .clickable { /* nav to Personal Info */ }
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(initials, style = MaterialTheme.typography.headlineMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                profile?.name ?: "Loading…",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))
        }

        /* list cards */
        fun listItem(icon: ImageVector, label: String, onClick: () -> Unit) {
            item {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(label, Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        listItem(Icons.Default.Person, "Personal info") {
            nav.navigate(Screen.Personalinfo.route)
        }
        listItem(Icons.Default.MailOutline, "Messages") {
            /* nav to offers / messages */
        }
        listItem(Icons.Default.Lock, "Login & security") {
            /* nav to security */
        }

        /* spacer so Refer friends scrolls down */
//        item { Spacer(Modifier.height(220.dp)) }

        listItem(Icons.Default.Star, "Refer friends, unlock deals") {
            /* share sheet or refer screen */
        }

        listItem(Icons.AutoMirrored.Filled.ExitToApp, "Log out") { showLogout = true }
    }
}