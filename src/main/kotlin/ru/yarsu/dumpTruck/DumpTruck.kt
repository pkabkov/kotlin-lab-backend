package ru.yarsu.dumpTruck

import java.time.LocalDateTime
import java.util.*

data class DumpTruck(
    val id : UUID,
    val model : String,
    val registration: String,
    val capacity: Double,
    val volume: Double
    )
