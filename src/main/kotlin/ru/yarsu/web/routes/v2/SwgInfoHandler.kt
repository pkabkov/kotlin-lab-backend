package ru.yarsu.web.routes.v2


import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGStorage
import ru.yarsu.web.lenses.paginationLens
import ru.yarsu.web.operations.v2.SwgListOperation

class SwgInfoHandler(private val swgStorage: SWGStorage) : HttpHandler {
    private val swgLens = Body.auto<SWG>().toLens()
    private val errorLens = Body.auto<Map<String, String>>().toLens()

    override fun invoke(request: Request): Response {
        val idLens = Path.uuid().of("shipment-id")
        try {
            val id = idLens(request)
        } catch (e: Exception) {
            return Response(Status.BAD_REQUEST).with(errorLens of mapOf("Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено текстовое значение"))
        }

        val id = idLens(request)
        if (swgStorage[id] == null) {
            return Response(Status.NOT_FOUND).with(
                errorLens of
                        mapOf(
                            "ShipmentId" to id.toString(),
                            "Error" to "Акт отгрузки ПГС не найден"
                        )
            )
        }
        return Response(Status.OK).with(swgLens of swgStorage[id]!!)
    }
}
