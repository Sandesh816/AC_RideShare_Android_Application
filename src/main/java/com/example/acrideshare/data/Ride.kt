package com.example.acrideshare.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.util.Date

@Parcelize
@Keep
data class Ride(
    val rideID: String = "",
    val creatorID: String = "",
    val originCity: String = "",
    val destinationCity: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val startDate: Date = Date(),
    val passengersList: List<String> = emptyList(),
    val status: String = "OPEN",
    val seatsAvailable: Int = 0,
    val creatorName: String = ""
) : Parcelable

