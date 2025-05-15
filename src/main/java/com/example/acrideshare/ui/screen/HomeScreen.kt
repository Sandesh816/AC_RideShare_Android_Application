package com.example.acrideshare.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.data.repo.AuthRepo
import com.example.acrideshare.ui.navigation.Screen

@Composable
fun HomeScreen(nav: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nav.navigate(Screen.SearchRides.route) }
        ) {
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Where to?", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "\"Share a ride, share the vibe.\"",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    nav: NavController,
//    vm: HomeScreenViewModel = hiltViewModel()
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("AC RideShare") },
//                actions = {
//                    IconButton(onClick = {
//                        vm.signOut()
//                        nav.navigate(Screen.Login.route) {
//                            popUpTo(0)  // clears backstack
//                        }
//                    }) {
//                        Icon(Icons.Default.ExitToApp, contentDescription = "Home")
//                    }
//                    TextButton(onClick = {
//                        vm.signOut()
//                        nav.navigate(Screen.Login.route) {
//                            popUpTo(0)  // clears backstack
//                        }
//                    }) {
//                        Text("Sign Out")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
//                )
//            )
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            Column(
//                modifier = Modifier.align(Alignment.Center),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalAlignment = CenterHorizontally
//            ) {
//                Button(onClick = { nav.navigate(Screen.SearchRides.route) }) {
//                    Text("Search Rides")
//                }
//                Button(onClick = { nav.navigate(Screen.CreateRide.route) }) {
//                    Text("Publish a Ride")
//                }
//                Button(onClick = { nav.navigate(Screen.MyRides.route) }) {
//                    Text("My Rides")
//                }
//            }
//        }
//    }
//}