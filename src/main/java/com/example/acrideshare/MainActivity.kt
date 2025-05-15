package com.example.acrideshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.acrideshare.ui.navigation.RideNavHost
import com.example.acrideshare.ui.navigation.Screen
import com.example.acrideshare.ui.theme.ACRideShareTheme
import com.example.acrideshare.ui.theme.RideTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RideTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val startDestination = if (FirebaseAuth.getInstance().currentUser == null) {
        Screen.Login
    } else {
        Screen.Home
    }


    RideNavHost(
        navController = navController,
        start = startDestination
    )
}
