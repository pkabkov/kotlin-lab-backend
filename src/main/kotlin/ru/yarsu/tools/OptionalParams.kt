package ru.yarsu.tools

import ru.yarsu.swg.SWGType
import java.time.LocalDate
import java.util.UUID

data class OptionalParams(
    val id: UUID? = null,
    val swgType: SWGType? = null,
    val startTime: LocalDate? = null,
    val endTime: LocalDate? = null,
    val year: Int? = null,
    val page: Int? = null,
    val recordsPerPage: Int? = null,
)
