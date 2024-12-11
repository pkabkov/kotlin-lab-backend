package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGStorage
import ru.yarsu.web.operations.v2.DumpTruckInfoOperation

class DumpTruckDeleteHandler(
    private val swgStorage: SWGStorage,
    private val dumpTruckStorage: DumpTruckStorage,
) : HttpHandler {
    private val errorLens = Body.auto<Map<String, String>>().toLens()

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

            if (swgStorage.getValues().any { it.dumpTruck == id }){
                return Response(Status.FORBIDDEN)
            }

            dumpTruckStorage.remove(id)
            return Response(Status.NO_CONTENT)

        } catch (e: Exception) {
            return Response(Status.BAD_REQUEST).with(errorLens of mapOf("Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено текстовое значение"))
        }
    }
}
