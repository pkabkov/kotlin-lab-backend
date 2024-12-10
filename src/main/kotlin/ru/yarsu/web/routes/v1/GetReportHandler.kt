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
import ru.yarsu.tools.validateYear
import ru.yarsu.web.operations.v1.GetReportOperation

class GetReportHandler(
    private val getReportOperation: GetReportOperation,
    private val mapper: Mapper,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val params = request.uri.queries()
        val yearParam = params.findSingle("year")

        try {
            validateYear(yearParam)
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(mapper.fromMapToJSONString(mapOf("Error" to e.message)))
        }
        val year = yearParam?.toInt()
        val info: List<Any> = getReportOperation.get(OptionalParams(year = year))
        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(mapper.fromMapToJSONString(info))
    }
}
