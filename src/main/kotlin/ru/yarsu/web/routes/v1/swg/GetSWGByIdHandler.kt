//package ru.yarsu.web.routes.v1.swg
//
//import org.http4k.core.ContentType
//import org.http4k.core.HttpHandler
//import org.http4k.core.Request
//import org.http4k.core.Response
//import org.http4k.core.Status
//import org.http4k.lens.contentType
//import org.http4k.routing.path
//import ru.yarsu.tools.Mapper
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.tools.validateId
//import ru.yarsu.web.operations.v1.swg.GetSWGByIdOperation
//import java.util.UUID
//
//class GetSWGByIdHandler(
//    private val getSWGByIdOperation: GetSWGByIdOperation,
//    private val mapper: Mapper,
//) : HttpHandler {
//    override fun invoke(request: Request): Response {
//        val idStr: String? = request.path("shipment-id")
//
//        try {
//            validateId(idStr)
//        } catch (e: IllegalArgumentException) {
//            return Response(Status.BAD_REQUEST)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(mapper.fromMapToJSONString(mapOf("error" to e.message)))
//        }
//        val id = UUID.fromString(idStr)
//        val info: Map<String, Any> = getSWGByIdOperation.get(OptionalParams(id = id))
//        if (info == emptyMap<String, Any>()) {
//            return Response(Status.NOT_FOUND)
//                .contentType(ContentType.APPLICATION_JSON)
//                .body(
//                    mapper.fromMapToJSONString(
//                        mapOf(
//                            "shipment-id" to id,
//                            "error" to "Акт отгрузки ПГС не найден",
//                        ),
//                    ),
//                )
//        }
//        return Response(Status.OK)
//            .contentType(ContentType.APPLICATION_JSON)
//            .body(mapper.fromMapToJSONString(info))
//    }
//}
