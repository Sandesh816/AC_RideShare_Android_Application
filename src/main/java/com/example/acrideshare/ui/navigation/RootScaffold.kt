package com.example.acrideshare.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.acrideshare.ui.screen.account.AccountScreen
import com.example.acrideshare.ui.screen.HomeScreen
import com.example.acrideshare.ui.screen.ride.MyRidesScreen

sealed class RootTab(val route: String, val icon: ImageVector, val label: String) {
    object Home    : RootTab("home"   , Icons.Default.Search  , "Home")
    object Rides   : RootTab("rides"  , Icons.Default.List    , "Rides")
    object Account : RootTab("account", Icons.Default.Person  , "Account")
    companion object { val all = listOf(Home, Rides, Account) }
}

@Composable
fun RootScaffold(parentNav: NavController) {
    val tabNav = rememberNavController()
    val dest   by tabNav.currentBackStackEntryAsState()
    val curTab = RootTab.all.find { dest?.destination?.route == it.route } ?: RootTab.Home

    Scaffold(
        floatingActionButton = {
            if (curTab == RootTab.Home) {
                FloatingActionButton(onClick = { parentNav.navigate(Screen.CreateRide.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Publish")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar {
                RootTab.all.forEach { tab ->
                    NavigationBarItem(
                        selected = curTab == tab,
                        onClick  = { tabNav.navigate(tab.route) { popUpTo(0) } },
                        icon     = { Icon(tab.icon, null) },
                        label    = { Text(tab.label) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(tabNav, startDestination = RootTab.Home.route, Modifier.padding(pad)) {
            composable(RootTab.Home.route)    { HomeScreen   (parentNav) }
            composable(RootTab.Rides.route)   { MyRidesScreen(parentNav) }
            composable(RootTab.Account.route) { AccountScreen(parentNav) }
        }
    }
}