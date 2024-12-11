package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.swg.SWG
import ru.yarsu.web.operations.v2.DumpTruckInfoOperation

class DumpTruckInfoHandler(
    private val dumpTruckStorage: DumpTruckStorage,
    private val operation: DumpTruckInfoOperation
) : HttpHandler {
    private val errorLens = Body.auto<Map<String, String>>().toLens()
    private val responseLens = Body.auto<ObjectNode>().toLens()

    override fun invoke(request: Request): Response {
        val idLens = Path.uuid().of("dump-truck-id")
        try {
            val id = idLens(request)
            if (id == null || !dumpTruckStorage.has(id)) {
                return Response(Status.NOT_FOUND).with(
                    errorLens of mapOf(
                        "DumpTruckId" to id.toString(),
                        "Error" to "Самосвал не найден"
                    )
                )
            }
            val response = operation.execute(id)
            return Response(Status.OK).with(responseLens of response)

        } catch (e: Exception) {
            return Response(Status.BAD_REQUEST).with(errorLens of mapOf("Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено текстовое значение"))
        }
    }
}
