package ru.yarsu.web.operations

import ru.yarsu.tools.OptionalParams

fun cutListIntoPage(
    page: Int,
    recordsPerPage: Int,
    list: List<Any>,
): List<Any> {
    val start = (page - 1) * recordsPerPage
    val end = (start + recordsPerPage).coerceAtMost(list.size)
    if (start >= list.size) return emptyList()
    return list.subList(start, end)
}

interface IGetHTTPEmployeesMapOperation {
    fun <T> get(optionalParams: OptionalParams = OptionalParams()): Map<String, T>
}

interface IGetHTTPEmployeesListOperation {
    fun <T> get(optionalParams: OptionalParams = OptionalParams()): List<T>
}
