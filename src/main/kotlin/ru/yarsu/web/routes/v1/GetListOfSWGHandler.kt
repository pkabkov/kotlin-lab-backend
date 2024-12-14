package ru.yarsu.web.routes.v1

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.findSingle
import org.http4k.core.queries
import org.http4k.lens.contentType
import ru.yarsu.tools.Mapper
import ru.yarsu.tools.OptionalParams
import ru.yarsu.tools.validatePage
import ru.yarsu.tools.validateRecordsPerPage
import ru.yarsu.web.operations.v1.GetListOfSWGOperation

private const val PAGE_DEFAULT = 1
private const val RECORDS_PER_PAGE = 10

class GetListOfSWGHandler(
    private val getListOfSWGOperation: GetListOfSWGOperation,
    private val mapper: Mapper,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val params = request.uri.queries()
        val pageParam = params.findSingle("page") ?: PAGE_DEFAULT
        val recordsPerPageParam = params.findSingle("records-per-page") ?: RECORDS_PER_PAGE

        try {
            validatePage(pageParam.toString())
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(mapper.fromMapToJSONString(mapOf("Error" to e.message)))
        }
        val page = pageParam.toString().toInt()

        try {
            validateRecordsPerPage(recordsPerPageParam.toString())
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(mapper.fromMapToJSONString(mapOf("Error" to e.message)))
        }
        val recordsPerPage = recordsPerPageParam.toString().toInt()

        val info =
            mapper
                .fromMapToJSONString(
                    getListOfSWGOperation.get<Any>(
                        OptionalParams(
                            page = page,
                            recordsPerPage = recordsPerPage,
                        ),
                    ),
                )
        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(info)
    }
}
