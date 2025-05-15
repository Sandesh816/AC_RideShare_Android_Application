package com.example.acrideshare.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val sentAt: Timestamp = Timestamp.now(),
    val readBy: List<String> = emptyList()      // UIDs that have seen the message
)