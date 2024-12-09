package ru.yarsu.tools

import ru.yarsu.swg.SWGType
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.UUID

fun validateCSVFile(fileStr: String?) {
    val file = File(fileStr.orEmpty())
    require(file.exists()) {
        "Передан несуществующий файл"
    }
    require(file.isFile) {
        "Передано название директории"
    }
    require(file.extension == "csv") {
        "Файл должен иметь расширение .csv"
    }
}

fun validateId(id: String?) {
    require(!id.isNullOrBlank()) {
        "Отсутствует обязательный параметр shipment-id"
    }
    try {
        UUID.fromString(id)
    } catch (_: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Некорректный идентификатор отгрузки ПГС. " +
                "Для параметра shipment-id ожидается UUID, " +
                "но получено значение «$id»",
        )
    }
}

fun validateSWGType(type: String?) {
    require(!type.isNullOrBlank()) {
        "Отсутствует обязательный параметр by-swg-type"
    }
    require(SWGType.entries.any { it.description == type }) {
        "Некорректный тип ПГС. " +
            "Для параметра by-swg-type ожидается тип ПГС, " +
            "но получено значение «$type»"
    }
}

fun validateTime(
    from: String?,
    to: String?,
) {
    if (from != null) {
        try {
            LocalDate.parse(from)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Некорректная дата. " +
                    "Для параметра from ожидается дата, " +
                    "но получено значение «$from»",
            )
        }
    }

    if (to != null) {
        try {
            LocalDate.parse(to)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Некорректная дата. " +
                    "Для параметра to ожидается дата, " +
                    "но получено значение «$to»",
            )
        }
    }

    require(from != null && to != null) {
        "Отсутствует обязательный параметр to или from."
    }

    val start = LocalDate.parse(from)
    val end = LocalDate.parse(to)
    require(start <= end) {
        "Время конца периода меньше времени начала"
    }
}

fun validateYear(yearStr: String?) {
    require(!yearStr.isNullOrBlank()) {
        "Отсутствует обязательный параметр year"
    }
    try {
        val year = yearStr.toInt()
        if (year <= 0) {
            throw NumberFormatException()
        }
    } catch (_: NumberFormatException) {
        throw IllegalArgumentException(
            "Некорректная дата. " +
                "Для параметра year ожидается натуральное число, " +
                "но получено значение «$yearStr»",
        )
    }
}

private const val MIN_PORT = 1024
private const val MAX_PORT = 65536

fun validatePort(port: Int?) {
    require(port in MIN_PORT..MAX_PORT) {
        "Неккоректный номер порта"
    }
}

fun validatePage(pageStr: String) {
    try {
        val page = pageStr.toInt()
        if (page < 1) throw NumberFormatException()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException(
            "Некорректное значение параметра page. " +
                "Ожидается натуральное число, " +
                "но получено $pageStr",
        )
    }
}

private const val RECORDS_PER_PAGE_SMALL = 5
private const val RECORDS_PER_PAGE_MEDIUM = 10
private const val RECORDS_PER_PAGE_LARGE = 20
private const val RECORDS_PER_PAGE_EXTRA_LARGE = 50
private val RECORD_PER_PAGE_ARRAY =
    intArrayOf(
        RECORDS_PER_PAGE_SMALL,
        RECORDS_PER_PAGE_MEDIUM,
        RECORDS_PER_PAGE_LARGE,
        RECORDS_PER_PAGE_EXTRA_LARGE,
    )

fun validateRecordsPerPage(recordsPerPageStr: String) {
    try {
        val recordsPerPage = recordsPerPageStr.toInt()
        if (recordsPerPage !in RECORD_PER_PAGE_ARRAY) throw NumberFormatException()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException(
            "Некорректное значение параметра records-per-page. " +
                "Ожидается число из спика: 5, 10, 20, 50, " +
                "но получено $recordsPerPageStr",
        )
    }
}
