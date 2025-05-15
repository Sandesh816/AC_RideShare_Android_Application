package com.example.acrideshare.ui.screen.ride

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRideScreen(
    nav: NavController,
    vm: CreateRideScreenViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    /* ── redirect on success ─────────────────── */
    if (ui.created) {
        LaunchedEffect(Unit) { nav.navigate(Screen.MyRides.route) }
    }

    /* ── snackbar host ───────────────────────── */
    val snack = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        topBar = {
            TopAppBar(
                title = { Text("Publish a ride", style = MaterialTheme.typography.headlineSmall)},
                navigationIcon = {
                    IconButton(onClick = { nav.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }}) {
                        Icon(
                            imageVector = Icons.AutoMirrored. Filled. ArrowBack,
                            contentDescription = "Home"
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
    ){ pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
//            Text("Publish a ride", style = MaterialTheme.typography.headlineSmall)
            Text("Let’s fill every seat! 🚗", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(24.dp))

            /* ── From / To ─────────────────────── */
            CityDropdown("Leaving from…", ui.origin, vm::onOrigin)
            Spacer(Modifier.height(8.dp))
            CityDropdown("Heading to…", ui.destination, vm::onDestination)

            Spacer(Modifier.height(16.dp))

            /* ── Seats stepper ─────────────────── */
            SeatStepper(seats = ui.seats, onDec = vm::decSeats, onInc = vm::incSeats)

            Spacer(Modifier.height(16.dp))

            /* ── Date & time pickers ───────────── */
            DateTimePickers(ui, vm)

            Spacer(Modifier.height(24.dp))

            /* ── Publish button ───────────────── */
            Button(
                onClick  = vm::createRide,
                enabled  = ui.canCreate,
                modifier = Modifier.fillMaxWidth()
            ) { Text("🚀 Publish ride") }

            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            if (ui.loading) BlockingSpinner()
        }
    }
}

/* ────────────────── helper composables ───────────────────────────── */

@Composable
private fun SeatStepper(seats: Int, onDec: () -> Unit, onInc: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        modifier = Modifier
            .height(48.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            IconButton(
                onClick = onDec,
                enabled = seats > 1,
                modifier = Modifier.size(32.dp)
            ) { Icon(Icons.Default.KeyboardArrowDown, null) }

            Text("$seats seat${if (seats > 1) "s" else ""}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp))

            IconButton(
                onClick = onInc,
                modifier = Modifier.size(32.dp)
            ) { Icon(Icons.Default.Add, null) }
        }
    }
}

@Composable
private fun DateTimePickers(ui: CreateRideScreenViewModel.UiState, vm: CreateRideScreenViewModel) {
    val context  = LocalContext.current
    val cal      = remember { Calendar.getInstance() }
    val dateFmt  = remember { SimpleDateFormat("yyyy‑MM‑dd", Locale.getDefault()) }
    val timeFmt  = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // date dialog
    val dateDlg = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                vm.onDate(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    // time dialog
    val timeDlg = remember {
        TimePickerDialog(context, { _, h, min ->
            cal.set(Calendar.HOUR_OF_DAY, h); cal.set(Calendar.MINUTE, min)
            vm.onTime(Timestamp(cal.time))
        }, cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE], false)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton({ dateDlg.show() }) {
            Icon(Icons.Default.DateRange, null); Spacer(Modifier.width(6.dp))
            Text(dateFmt.format(ui.date))
        }
        OutlinedButton({ timeDlg.show() }) {
//            Icon(Icons.AutoMirrored.Filled., null); Spacer(Modifier.width(6.dp))
            Text(timeFmt.format(ui.time.toDate()))
        }
    }
}

@Composable private fun BlockingSpinner() =
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreateRideScreen(nav: NavController, modifier: Modifier = Modifier, vm: CreateRideScreenViewModel = hiltViewModel()) {
//    val ui by vm.ui.collectAsState()
//    if (ui.created) {
//        LaunchedEffect(Unit) { nav.navigate(Screen.MyRides.route) }
//    }
//
//    val snackbarHostState = remember { SnackbarHostState() }
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Box(modifier
//            .fillMaxSize()
//            .padding()
//        ) {
//            Column(
//                modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 24.dp)
//                    .align(Alignment.Center),
//                horizontalAlignment = CenterHorizontally
//            ) {
//                TopAppBar(
//                    title = { Text("Publish a ride") },
//                    navigationIcon = {
//                        IconButton(onClick = { nav.navigate(Screen.Home.route) {
//                            popUpTo(Screen.Home.route) { inclusive = true }
//                        }}) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored. Filled. ArrowBack,
//                                contentDescription = "Home"
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
//                    )
//                )
//
//                // Origin and Destination cities dropdown
//                CityDropdown("From", ui.origin, vm::onOrigin)
//                CityDropdown("To", ui.destination, vm::onDestination)
//
//                // Seat stepper
//                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp).height(IntrinsicSize.Min)) {
//                    Text("Seats: ${ui.seats}", modifier = Modifier.align(Alignment.CenterVertically).fillMaxHeight())
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Column(
//                        modifier = Modifier.fillMaxHeight()
//                    ) {
//                        IconButton(onClick = vm::decSeats) { Icon(Icons.Default.KeyboardArrowDown, null) }
//                        IconButton(onClick = vm::incSeats) { Icon(Icons.Default.KeyboardArrowUp, null) }
//                    }
//                }
//
//                // Date and time
//                val context = LocalContext.current
//                val calendar = remember { Calendar.getInstance() }
//
//                LaunchedEffect(ui.date, ui.time) {
//                    calendar.time = ui.date
//                    ui.time.toDate().let { t ->
//                        calendar.set(Calendar.HOUR_OF_DAY, t.hours)
//                        calendar.set(Calendar.MINUTE, t.minutes)
//                    }
//                }
//
//                val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
//                val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
//
//                // dialogs to pick date and time
//                val datePicker = remember {
//                    DatePickerDialog(
//                        context,
//                        { _, year, month, dayOfMonth ->
//                            calendar.set(year, month, dayOfMonth)
//                            vm.onDate(calendar.time)
//                        },
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)
//                    )
//                }
//
//                val timePicker = remember {
//                    TimePickerDialog(
//                        context,
//                        { _, hourOfDay, minute ->
//                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                            calendar.set(Calendar.MINUTE, minute)
//                            vm.onTime(Timestamp(calendar.time))
//                        },
//                        calendar.get(Calendar.HOUR_OF_DAY),
//                        calendar.get(Calendar.MINUTE),
//                        false  // true: 24-hour view; false: AM/PM
//                    )
//                }
//
//                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    OutlinedButton(onClick = { datePicker.show() }) {
//                        Text("Date: ${dateFormat.format(ui.date)}")
//                    }
//                    OutlinedButton(onClick = { timePicker.show() }) {
//                        Text("Time: ${timeFormat.format(ui.time.toDate())}")
//                    }
//                }
//
//                Spacer(Modifier.height(12.dp))
//                Button(onClick = vm::createRide, enabled = ui.canCreate) { Text("Publish Ride") }
//                ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
//
//                if (ui.loading) {
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
//                    ) {
//                        CircularProgressIndicator(Modifier.align(Alignment.Center))
//                    }
//                }
//            }
//        }
//    }
//}

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