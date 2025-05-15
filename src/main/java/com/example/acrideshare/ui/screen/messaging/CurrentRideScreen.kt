package com.example.acrideshare.ui.screen.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.acrideshare.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentRideScreen(
    nav: NavController,
    vm: CurrentRideScreenViewModel = hiltViewModel()
) {
    val ride   by vm.ride.collectAsState()
    val msgs   by vm.messages.collectAsState()

    if (ride == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val r = ride!!

    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(msgs.size) {
        listState.animateScrollToItem(maxOf(msgs.size - 1, 0))
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("${r.originCity} ➔ ${r.destinationCity}")
                        Text(
                            "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(r.startTime.toDate())} • " +
                                    SimpleDateFormat("yyyy‑MM‑dd", Locale.getDefault()).format(r.startDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        nav.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) { Icon(Icons.Default.Home, null) }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {

            /* CHAT LIST */
            LazyColumn(
                state       = listState,
                modifier    = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(msgs, key = { it.id }) { m ->
                    val mine = m.senderId == vm.uid
                    Row(
                        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (mine) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text(
                                    m.text,
                                    color = if (mine) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(m.sentAt.toDate()),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    if (mine) {
                                        Spacer(Modifier.width(4.dp))
                                        val read = m.readBy.size - 1
                                        Text("✓✓ $read", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* INPUT */
            Row(
                Modifier
                    .padding(8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message…") },
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        vm.send(text)
                        text = ""
                    },
                    enabled = text.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
    }
}




//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CurrentRideScreen(nav: NavController, vm: CurrentRideScreenViewModel = hiltViewModel()) {
//    val currentRideState by vm.rideState.collectAsState()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    LaunchedEffect(currentRideState) {
//        if (currentRideState is CurrentRideScreenViewModel.RideState.Error) {
//            launch {
//                val res = snackbarHostState.showSnackbar(
//                    message = "Ride not found or unknown error",
//                    actionLabel = "Dismiss",
//                    duration = SnackbarDuration.Indefinite
//                )
//                nav.popBackStack()
//            }
//        }
//    }
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Box(
//            Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 24.dp)
//                    .align(Alignment.Center),
//                horizontalAlignment = CenterHorizontally
//            ) {
//                when (currentRideState) {
//                    is CurrentRideScreenViewModel.RideState.Loading -> {
//                        LinearProgressIndicator(Modifier.fillMaxSize())
//                    }
//
//                    is CurrentRideScreenViewModel.RideState.Success -> {
//                        val currentRide =
//                            (currentRideState as CurrentRideScreenViewModel.RideState.Success).ride
//                        TopAppBar(
//                            title = { Text("${currentRide.originCity} -> ${currentRide.destinationCity} at ${currentRide.startTime} on ${currentRide.startDate}") },
//                            navigationIcon = {
//                                IconButton(onClick = { nav.popBackStack() }) {
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                        contentDescription = "Back"
//                                    )
//                                }
//                            },
//                            actions = {
//                                IconButton(onClick = {
//                                    nav.navigate(Screen.Home.route) {
//                                        popUpTo(Screen.Home.route) { inclusive = true }
//                                    }
//                                }) {
//                                    Icon(Icons.Filled.Home, contentDescription = "Home")
//                                }
//                            },
//                            colors = TopAppBarDefaults.topAppBarColors(
//                                containerColor = MaterialTheme.colorScheme.primary,
//                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
//                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
//                            )
//                        )
//                    }
//
//                    is CurrentRideScreenViewModel.RideState.Error -> {
//                        Text("Loading ride...")
//                    }
//
//                    is CurrentRideScreenViewModel.RideState.Idle -> {
//                        Text("Loading ride...")
//                    }
//                }
//            }
//        }
//    }
//}



//    val msgs by vm.messages.collectAsState()
//    var text by remember { mutableStateOf("") }
//
//    Column(Modifier.fillMaxSize()) {
//        LazyColumn(Modifier.weight(1f).padding(8.dp)) {
//            items(msgs) { m ->
//                Text("${m.senderId.take(4)}: ${m.text}")
//            }
//        }
//        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
//            OutlinedTextField(text, onValueChange = { text = it }, modifier = Modifier.weight(1f), placeholder = { Text("Message…") })
//            IconButton(onClick = {
//                if (text.isNotBlank()) {
//                    vm.send(text); text = ""
//                }
//            }) {
//                Icon(Icons.Default.Send, contentDescription = "Send")
//            }
//        }
//    }
