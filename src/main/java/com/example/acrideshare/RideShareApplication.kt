package com.example.acrideshare

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.acrideshare.ui.navigation.RideNavHost
import com.example.acrideshare.ui.navigation.Screen
import com.example.acrideshare.ui.theme.ACRideShareTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RideshareAppplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.setLoggingEnabled(true)
    }
}

/*
*     val navController = rememberNavController()
    ACRideShareTheme{
        RideNavHost(navController,
            start = if (FirebaseAuth.getInstance().currentUser == null)
                Screen.Login else Screen.Home)
    }
*
*
* */
