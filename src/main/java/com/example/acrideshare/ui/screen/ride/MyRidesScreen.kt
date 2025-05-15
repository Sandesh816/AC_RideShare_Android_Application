package com.example.acrideshare.ui.screen.ride

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.data.Ride
import com.example.acrideshare.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRidesScreen(
    nav: NavController,
    vm: MyRidesViewModel = hiltViewModel()
) {
    val upcoming by vm.myUpcomingRides.collectAsState()
    val past     by vm.myPastRides.collectAsState()

    /* tab state */
    var selectedTab by remember { mutableStateOf(0) } // 0 = upcoming, 1 = past
    val rides = if (selectedTab == 0) upcoming else past

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Need a lift? Search!") },
                icon = { Icon(Icons.Default.Search, null) },
                onClick = { nav.navigate(Screen.SearchRides.route) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
        ) {

            /* ── Minimal top bar ── */
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Rides", style = MaterialTheme.typography.headlineSmall)
            }

            /* ── Tab row ── */
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Upcoming") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("Past") }
                )
            }

            /* ── Empty state ── */
            if (rides.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (selectedTab == 0)
                            "No upcoming rides. Publish or join one!"
                        else
                            "No road‑trips on record. Memories await 🚗"
                    )
                }
                return@Column
            }

            /* ── Ride list ── */
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rides, key = { it.rideID }) { ride ->
                    RideCard(
                        ride       = ride,
                        canJoin    = false,
                        onJoin     = {},
                        showDelete = selectedTab == 0 && vm.uid == ride.creatorID,
                        onDelete   = { vm.deleteRide(ride) },
                        showLeave  = selectedTab == 0 && vm.uid in ride.passengersList,
                        onLeave    = { vm.leaveRide(ride) },
                        onClick    = {
                            nav.navigate(Screen.CurrentRide.create(ride.rideID))
                        },
                        _vm         = vm,             // if RideCard’s name lookup isn’t needed
                        showJoin   = false
                    )
                }
            }
        }
    }
}


@Composable
private fun RideCard(
    ride: Ride,
    canJoin: Boolean,
    onJoin: () -> Unit,
    showDelete: Boolean,
    onDelete: () -> Unit,
    showLeave: Boolean,
    onLeave: () -> Unit,
    onClick: () -> Unit,
    _vm: MyRidesViewModel,
    showJoin: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .animateContentSize()        // smooth expand
    ) {
        Column(Modifier.padding(16.dp)) {

            /* origin ➔ destination row */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(ride.originCity, style = MaterialTheme.typography.titleMedium)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.padding(horizontal = 8.dp))
//                Icon(Icons.Default., null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text(ride.destinationCity, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(4.dp))
            Row(){
                Text(
                    SimpleDateFormat("EEE, d MMM • HH:mm", Locale.getDefault()).format(ride.startTime.toDate()),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(18.dp))
                Text(
                    text = "Creator: ${ride.creatorName}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                "Seats left: ${ride.seatsAvailable - ride.passengersList.size}/${ride.seatsAvailable}",
                style = MaterialTheme.typography.labelMedium
            )

            /* action row */
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showDelete)
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }

                if (showJoin)
                    TextButton(onClick = onJoin, enabled = canJoin) { Text("Join") }

                if (showLeave)
                    TextButton(onClick = onLeave) { Text("Leave") }

                Spacer(Modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand or Close"
                    )
                }
            }
            /* passenger list */
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 4.dp)) {
                    Text("Passengers:", style = MaterialTheme.typography.labelLarge)
                    ride.passengersList.forEach { uid ->
                        val name by produceState("…") { value = _vm.displayName(uid) }
                        Text("• $name")
                    }
                }
            }
        }
    }
}