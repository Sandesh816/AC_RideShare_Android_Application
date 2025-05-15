package com.example.acrideshare.ui.navigation

sealed class Screen(val route: String){
    object Login         : Screen("login")
    object SignUp        : Screen("signup")
    object ForgotPwd     : Screen("forgot")
    object Home          : Screen("home")
    object CreateRide    : Screen("createRide")
    object SearchRides   : Screen("searchRides")
    object MyRides       : Screen("myRides")
    object CurrentRide   : Screen("currentRide/{rideId}"){
        fun create(id:String) = "currentRide/$id"
    }
    object Personalinfo          : Screen("Personalinfo")
    object UpdateName    : Screen("UpdateName")
    object UpdatePhone   : Screen("UpdatePhone")
    object UpdateEmail       : Screen("UpdateEmail")
}