package com.example.acrideshare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.acrideshare.ui.screen.account.PersonalinfoScreen
import com.example.acrideshare.ui.screen.account.UpdateEmailScreen
import com.example.acrideshare.ui.screen.account.UpdateNameScreen
import com.example.acrideshare.ui.screen.account.UpdatePhoneScreen
import com.example.acrideshare.ui.screen.ride.CreateRideScreen
import com.example.acrideshare.ui.screen.messaging.CurrentRideScreen
import com.example.acrideshare.ui.screen.logging.ForgotPwdScreen
import com.example.acrideshare.ui.screen.logging.LoginScreen
import com.example.acrideshare.ui.screen.ride.MyRidesScreen
import com.example.acrideshare.ui.screen.ride.SearchRidesScreen
import com.example.acrideshare.ui.screen.logging.SignUpScreen

@Composable
fun RideNavHost(navController: NavHostController, start: Screen = Screen.Login) {
    NavHost(navController, startDestination = start.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.ForgotPwd.route){ ForgotPwdScreen(navController) }
//        composable(Screen.Home.route)    { HomeScreen(navController) }
        composable(Screen.Home.route)    { RootScaffold(navController) }

        composable(Screen.CreateRide.route){ CreateRideScreen(navController) }
        composable(Screen.SearchRides.route){ SearchRidesScreen(navController) }
        composable(Screen.MyRides.route){ MyRidesScreen(navController) }
        composable(Screen.CurrentRide.route,
            arguments = listOf(navArgument("rideId"){type= NavType.StringType})) {
            CurrentRideScreen(navController)
        }
        composable(Screen.Personalinfo.route)  { PersonalinfoScreen(navController) }
        composable(Screen.UpdateName.route)  { UpdateNameScreen(navController) }
        composable(Screen.UpdateEmail.route)  { UpdateEmailScreen(navController) }
        composable(Screen.UpdatePhone.route)  { UpdatePhoneScreen(navController) }
    }
}