package com.example.acrideshare.ui.screen.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalinfoScreen(nav: NavController, vm: AccountScreenViewModel = hiltViewModel()) {
    val profile by vm.profile.collectAsState()

    val name = profile?.name ?: "Name"
    val phone = profile?.phone ?: "Phone"
    val email = profile?.email ?: "Email"

    val snackbarHostState = remember {SnackbarHostState()  }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.popBackStack()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored. Filled. ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ){ pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 32.dp, bottom = 90.dp)
        ) {

            /* avatar + name */
            item {
                Text(
                    "Personal info",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
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

            listItem(Icons.Default.Person, "${name}") {
                nav.navigate(Screen.UpdateName.route)
            }
            listItem(Icons.Default.Call, "${phone}") {
                nav.navigate(Screen.UpdatePhone.route)
            }
            listItem(Icons.Default.MailOutline, "${email}") {
                nav.navigate(Screen.UpdateEmail.route)
            }

            /* spacer so Refer friends scrolls down */
//        item { Spacer(Modifier.height(220.dp)) }

            listItem(Icons.Default.Star, "Refer friends, unlock deals") {

            }

        }
    }
}

}

