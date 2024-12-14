package ru.yarsu.web.routes.v2


import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGStorage

class SwgInfoHandler(
    private val swgStorage: SWGStorage,
    private val dumpTruckStorage: DumpTruckStorage,
    private val employeeStorage: EmployeeStorage
) : HttpHandler {
    private val swgLens = Body.auto<ObjectNode>().toLens()
    private val errorLens = Body.auto<Map<String, String>>().toLens()

    private val json = Jackson
    private val jsonFactory = JsonNodeFactory.instance

    override fun invoke(request: Request): Response {
        val idLens = Path.uuid().of("shipment-id")
        try {
            val id = idLens(request)
        } catch (e: Exception) {
            return Response(Status.BAD_REQUEST).with(errorLens of mapOf("Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено текстовое значение"))
        }

        val id = idLens(request)
        val swg = swgStorage[id]
        val response = jsonFactory.objectNode()

        if (swg == null) {
            return Response(Status.NOT_FOUND).with(
                errorLens of
                        mapOf(
                            "ShipmentId" to id.toString(),
                            "Error" to "Акт отгрузки ПГС не найден"
                        )
            )
        } else {
            response.set<ObjectNode>("Id", json.string(swg.id.toString()))
            response.set<ObjectNode>("Title", json.string(swg.title))
            response.set<ObjectNode>("Measure", json.string(swg.measure.description))
            response.set<ObjectNode>("Count", json.number(swg.count))
            response.set<ObjectNode>("Price", json.number(swg.price))
            response.set<ObjectNode>("Cost", json.number(swg.cost))
            response.set<ObjectNode>("ShipmentDateTime", json.string(swg.shipmentDateTime.toString()))
            response.set<ObjectNode>(
                "DumpTruckRegistration",
                json.string(dumpTruckStorage[swg.dumpTruck]?.model.toString())
            )
            response.set<ObjectNode>("Washing", json.boolean(swg.washing))
            response.set<ObjectNode>("ManagerEmail", json.string(employeeStorage[swg.manager]?.email.toString()))
        }
        return Response(Status.OK).with(swgLens of response)
    }
}
