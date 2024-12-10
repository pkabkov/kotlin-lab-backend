package ru.yarsu.web.lenses

import org.http4k.lens.*

data class PaginationParameters(val page: Int, val recordsPerPage: Int)

private val pageLens = Query.int().map(
    { page ->
        if (page < 1) {
            throw LensFailure(message = "Некорректное значение параметра page. Ожидается натуральное число.")
        }
        page
    },
    {page -> page}
).defaulted("page", 1)

private val recordsPerPage  = Query.int().map(
    { records ->
        val corrects = intArrayOf(5, 10, 20, 50)
        if (records !in corrects){
            throw LensFailure(message = "Некорректное значение параметра records-per-page. Ожидается число из спика: 5, 10, 20, 50")
        }
        records
    },
    {records -> records}
).defaulted("records-per-page", 10)

val paginationLens = Query.composite{request ->
    PaginationParameters(
        pageLens(request),
        recordsPerPage(request)
    )
}
