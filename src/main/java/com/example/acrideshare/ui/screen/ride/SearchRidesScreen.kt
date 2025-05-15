package com.example.acrideshare.ui.screen.ride

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.data.Ride
import com.example.acrideshare.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRidesScreen(
    nav: NavController,
    vm: SearchRidesScreenViewModel = hiltViewModel()
) {
    val rides by vm.uiRides.collectAsState()
    val action by vm.rideActionState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    /* ── filters state ───────────────────────── */
    var from by remember { mutableStateOf("") }
    var to   by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<Date?>(null) }

    val context  = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val df = remember { SimpleDateFormat("yyyy‑MM‑dd", Locale.getDefault()) }

    /* ── snackbar ────────────────────────────── */
    val snack = remember { SnackbarHostState() }
    LaunchedEffect(action) {
        when (action) {
            is SearchRidesScreenViewModel.RideActionState.Success ->
                snack.showSnackbar((action as SearchRidesScreenViewModel.RideActionState.Success).message)
            is SearchRidesScreenViewModel.RideActionState.Error ->
                snack.showSnackbar((action as SearchRidesScreenViewModel.RideActionState.Error).message)
            else -> {}
        }
        vm.resetRideActionState()
    }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                calendar.set(y, m, d)
                val picked = calendar.time
                date = picked
                vm.onDate(picked)
                showDatePicker = false // Hide the dialog after a date is picked
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
        }.show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = {
            TopAppBar(
                title = { Text("Find your next ride 🚗") },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.popBackStack()
                    })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
        ) {
            /* Search header */
//            Text("Find your next ride 🚗", style = MaterialTheme.typography.headlineSmall)
//            Spacer(Modifier.height(8.dp))

            CityDropdown("From", from) { from = it; vm.onOrigin(it) }
            Spacer(Modifier.height(4.dp))
            CityDropdown("To", to) { to = it; vm.onDestination(it) }
            Spacer(Modifier.height(4.dp))

            /* Date picker */
            OutlinedButton(onClick = {
                showDatePicker = true
//                pickDate(initial = date, cb = {}) { date = it; vm.onDate(it) }
            }) {
                Icon(Icons.Default.DateRange, null)
                Spacer(Modifier.width(8.dp))
                Text(date?.let { df.format(it) } ?: "Any date")
            }

            Spacer(Modifier.height(12.dp))
            if (rides.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No rides match. Try changing filters")
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(rides, key = { it.rideID }) { ride ->
                        RideCard(
                            ride       = ride,
                            canJoin    = vm.canJoin(ride),
                            onJoin     = { vm.requestJoin(ride) },
                            showDelete = vm.uid == ride.creatorID,
                            onDelete   = { vm.deleteRide(ride) },
                            showLeave  = vm.uid in ride.passengersList,
                            onLeave    = { vm.leaveRide(ride) },
                            onClick    = { if (vm.isMember(ride)) nav.navigate(Screen.CurrentRide.create(ride.rideID)) },
                            vm         = vm,
                            showJoin = (vm.uid != ride.creatorID && !(vm.uid in ride.passengersList)),
                        )
                    }
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
    vm: SearchRidesScreenViewModel,
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
                        val name by produceState("…") { value = vm.displayName(uid) }
                        Text("• $name")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityDropdown(label: String, selection: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selection,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CityOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option); expanded = false
                })
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchRidesScreen(
//    nav: NavController,
//    modifier: Modifier = Modifier,
//    vm: SearchRidesScreenViewModel = hiltViewModel()
//) {
//    val rides by vm.uiRides.collectAsState()
//    val rideActionState by vm.rideActionState.collectAsState()
//
//    var originSel by remember { mutableStateOf("") }
//    var destSel by remember { mutableStateOf("") }
//    var dateSel by remember { mutableStateOf<Date?>(null) }
//
//    val context = LocalContext.current
//    val calendar = remember { Calendar.getInstance() }
//    val df = remember { SimpleDateFormat("yyyy-MM-dd") }
//
//    val datePicker = remember {
//        DatePickerDialog(
//            context,
//            { _, y, m, d ->
//                calendar.set(y, m, d)
//                val picked = calendar.time
//                dateSel = picked
//                vm.onDate(picked)
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        )
//    }
//    val snackbarHostState = remember { SnackbarHostState() }
//    LaunchedEffect(rideActionState) {
//        when (rideActionState) {
//            is SearchRidesScreenViewModel.RideActionState.Success -> {
//                val message =
//                    (rideActionState as SearchRidesScreenViewModel.RideActionState.Success).message
//                snackbarHostState.showSnackbar(
//                    message = message,
//                    duration = SnackbarDuration.Short
//                )
//                vm.resetRideActionState()
//            }
//
//            is SearchRidesScreenViewModel.RideActionState.Error -> {
//                val message =
//                    (rideActionState as SearchRidesScreenViewModel.RideActionState.Error).message
//                snackbarHostState.showSnackbar(
//                    message = message,
//                    duration = SnackbarDuration.Long
//                )
//                vm.resetRideActionState()
//            }
//
//            else -> {}
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                title = { Text("Search Rides") },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        nav.navigate(Screen.Home.route) {
//                            popUpTo(Screen.Home.route) { inclusive = true }
//                        }
//                    })
//                    {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Home"
//                        )
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
//            Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 16.dp),
//                horizontalAlignment = CenterHorizontally
//            ) {
//                CityDropdown("From", originSel) {
//                    originSel = it
//                    vm.onOrigin(it)
//                }
//                Spacer(Modifier.height(4.dp))
//                CityDropdown("To", destSel) {
//                    destSel = it
//                    vm.onDestination(it)
//                }
//                Spacer(Modifier.height(4.dp))
//                OutlinedButton(onClick = { datePicker.show() }) {
//                    Text(text = dateSel?.let { df.format(it) } ?: "Any date")
//                }
//
//                Spacer(Modifier.height(12.dp))
//                if (rides.isEmpty()) {
//                    Text("No rides available", modifier = Modifier.padding(16.dp))
//                } else {
//                    LazyColumn(modifier.fillMaxSize()) {
//                        items(rides, key = { it.rideID }) { ride ->
//                            RideCard(
//                                ride = ride,
//                                canJoin = vm.canJoin(ride),
//                                onJoin = { vm.requestJoin(ride) },
//                                showDelete = vm.uid == ride.creatorID,
//                                onDelete = { vm.deleteRide(ride) },
//                                showLeave = vm.uid in ride.passengersList,
//                                onLeave = { vm.leaveRide(ride) },
//                                onClick = {
//                                    if (vm.isMember(ride)) {
//                                        nav.navigate(Screen.CurrentRide.create(ride.rideID))
//                                    }
//                                },
//                                showJoin = (vm.uid != ride.creatorID && !(vm.uid in ride.passengersList))
//                            )
//                            Spacer(Modifier.height(6.dp))
//                        }
//                    }
//                }
//                if (rideActionState is SearchRidesScreenViewModel.RideActionState.Loading) {
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
//                            .clickable(enabled = false) {  }
//                    ) {
//                        CircularProgressIndicator(Modifier.align(Center))
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun RideCard(
//    ride: Ride,
//    canJoin: Boolean,
//    onJoin: () -> Unit,
//    showDelete: Boolean,
//    onDelete: () -> Unit,
//    onClick: () -> Unit,
//    showLeave: Boolean,
//    onLeave: () -> Unit,
//    showJoin: Boolean
//) {
//    var expanded by remember { mutableStateOf(false) }
//    Card(Modifier
//        .fillMaxWidth()
//        .clickable(onClick = onClick)) {
//        Box(Modifier.fillMaxWidth()) {
//            if (showDelete) {
//                IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.TopEnd)) {
//                    Icon(Icons.Default.Delete, contentDescription = "Delete ride")
//                }
//            }
//            Column(Modifier.padding(12.dp)) {
//                Text("${ride.originCity} ➔ ${ride.destinationCity}", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                Text("${ride.startTime.toDate()}")
//                Text("Available Seats: ${ride.seatsAvailable - ride.passengersList.size} / ${ride.seatsAvailable}")
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    if (showJoin){
//                        Button(
//                            onClick = {
//                                onJoin()
//                            },
//                            enabled = canJoin,
//                            modifier = Modifier
//                                .align(Alignment.CenterVertically)
//                        ) { Text("Request to Join")}
//                    }
//                    if (showLeave){
//                        Button(
//                            onClick = onLeave,
//                            modifier = Modifier
//                                .align(Alignment.CenterVertically)
//                        ) { Text("Request to Leave")}
//                    }
//                    Spacer(Modifier.weight(1f))
//                    IconButton(onClick = { expanded = !expanded }) {
//                        Icon(
//                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
//                            else Icons.Filled.KeyboardArrowDown,
//                            contentDescription = "Expand or close"
//                        )
//                    }
//                }
//                AnimatedVisibility(expanded) {
//                    Column(Modifier.padding(start = 8.dp, top = 4.dp)) {
//                        Text("Passengers:")
//                        ride.passengersList.forEach { Text("• $it") }
//                    }
//                }
//            }
//        }
//    }
//}