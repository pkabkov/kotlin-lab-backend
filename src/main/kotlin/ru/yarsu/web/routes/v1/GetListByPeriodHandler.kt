//package ru.yarsu.web.routes.v1
//
//import org.http4k.core.ContentType
//import org.http4k.core.HttpHandler
//import org.http4k.core.Request
//import org.http4k.core.Response
//import org.http4k.core.Status
//import org.http4k.core.findSingle
//import org.http4k.core.queries
//import org.http4k.lens.contentType
//import ru.yarsu.tools.Mapper
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.tools.validatePage
//import ru.yarsu.tools.validateRecordsPerPage
//import ru.yarsu.tools.validateTime
//import ru.yarsu.web.operations.v1.GetListByPeriodOperation
//import java.time.LocalDate
//
//private const val PAGE_DEFAULT = 1
//private const val RECORDS_PER_PAGE = 10
//
//class GetListByPeriodHandler(
//    private val getListByPeriodOperation: GetListByPeriodOperation,
//    private val mapper: Mapper,
//) : HttpHandler {
//    override fun invoke(request: Request): Response {
//        val params = request.uri.queries()
//        val pageParam = params.findSingle("page") ?: PAGE_DEFAULT
//        val recordsPerPageParam = params.findSingle("records-per-page") ?: RECORDS_PER_PAGE
//        val startParam = params.findSingle("from")
//        val endParam = params.findSingle("to")
//
//        try {
//            validatePage(pageParam.toString())
//        } catch (e: IllegalArgumentException) {
//            return Response(Status.BAD_REQUEST)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(mapper.fromMapToJSONString(mapOf("error" to e.message)))
//        }
//        val page = pageParam.toString().toInt()
//
//        try {
//            validateRecordsPerPage(recordsPerPageParam.toString())
//        } catch (e: IllegalArgumentException) {
//            return Response(Status.BAD_REQUEST)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(mapper.fromMapToJSONString(mapOf("error" to e.message)))
//        }
//        val recordsPerPage = recordsPerPageParam.toString().toInt()
//
//        try {
//            validateTime(startParam, endParam)
//        } catch (e: IllegalArgumentException) {
//            return Response(Status.BAD_REQUEST)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(mapper.fromMapToJSONString(mapOf("error" to e.message)))
//        }
//        val start = LocalDate.parse(startParam.orEmpty())
//        val end = LocalDate.parse(endParam.orEmpty())
//
//        val info: List<Any> =
//            getListByPeriodOperation
//                .get(
//                    OptionalParams(
//                        page = page,
//                        recordsPerPage = recordsPerPage,
//                        startTime = start,
//                        endTime = end,
//                    ),
//                )
//        return Response(Status.OK)
//            .contentType(ContentType.APPLICATION_JSON)
//            .body(mapper.fromMapToJSONString(info))
//    }
//}
