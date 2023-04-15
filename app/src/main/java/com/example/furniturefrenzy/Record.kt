package com.example.furniturefrenzy

data class Record(
    val uuid: String,
    val dateTime: String,
    val timeTaken: Int,
    val score: Int,
    val userName: String? = null // Add the userName field
)

