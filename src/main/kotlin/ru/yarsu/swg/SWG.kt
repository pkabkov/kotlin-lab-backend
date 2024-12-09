package ru.yarsu.swg

import ru.yarsu.dumpTruck.DumpTruck
import java.time.LocalDateTime
import java.util.UUID

data class SWG(
    val id: UUID,
    val title: String,
    val swgType: SWGType,
    val measure: SWGMeasure,
    val count: Double,
    val price: Double,
    val cost: Double,
    val shipmentDateTime: LocalDateTime,
    val dumpTruck: UUID,
    val washing: Boolean,
    val manager: UUID,
)
