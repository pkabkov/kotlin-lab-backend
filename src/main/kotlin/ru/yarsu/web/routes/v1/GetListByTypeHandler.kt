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
//import ru.yarsu.swg.SWGType
//import ru.yarsu.tools.Mapper
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.tools.validatePage
//import ru.yarsu.tools.validateRecordsPerPage
//import ru.yarsu.tools.validateSWGType
//import ru.yarsu.web.operations.v1.GetListByTypeOperation
//
//private const val PAGE_DEFAULT = 1
//private const val RECORDS_PER_PAGE = 10
//
//class GetListByTypeHandler(
//    private val getListByTypeOperation: GetListByTypeOperation,
//    private val mapper: Mapper,
//) : HttpHandler {
//    override fun invoke(request: Request): Response {
//        val params = request.uri.queries()
//
//        val typeParam = params.findSingle("by-swg-type")
//        val pageParam = params.findSingle("page") ?: PAGE_DEFAULT
//        val recordsPerPageParam = params.findSingle("records-per-page") ?: RECORDS_PER_PAGE
//
//        try {
//            validateSWGType(typeParam)
//        } catch (e: IllegalArgumentException) {
//            return Response(Status.BAD_REQUEST)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(mapper.fromMapToJSONString(mapOf("error" to e.message)))
//        }
//        val type = SWGType.entries.find { it.description == typeParam }
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
//        val info: List<Any> =
//            getListByTypeOperation.get(OptionalParams(swgType = type, page = page, recordsPerPage = recordsPerPage))
//        return Response(Status.OK)
//            .contentType(ContentType.APPLICATION_JSON)
//            .body(mapper.fromMapToJSONString(info))
//    }
//}
