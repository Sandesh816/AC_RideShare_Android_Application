package com.example.acrideshare.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class User(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val uid: String = "",
)



