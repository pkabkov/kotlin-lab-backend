package ru.yarsu.web.operations.v2

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.format.Jackson
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWGStorage
import java.util.*

class DumpTruckInfoOperation(
    private val dumpTruckStorage: DumpTruckStorage,
    private val swgStorage: SWGStorage,
) {
    private val json = Jackson
    private val jsonFactory = JsonNodeFactory.instance

    fun execute(id: UUID): ObjectNode{
        val response = jsonFactory.objectNode()
        val truck = dumpTruckStorage[id]!!
        val shipmentsNode = jsonFactory.arrayNode()
        swgStorage.getValues().filter { it.dumpTruck == id }.forEach {
            val tmpNode = jsonFactory.objectNode()
            tmpNode.set<ObjectNode>("Id", json.string(it.id.toString()))
            tmpNode.set<ObjectNode>("Title", json.string(it.title))
            tmpNode.set<ObjectNode>("ShipmentDateTime", json.string(it.shipmentDateTime.toString()))
            shipmentsNode.add(tmpNode)
        }
        response.set<ObjectNode>("Id", json.string(truck.id.toString()))
        response.set<ObjectNode>("Model", json.string(truck.model))
        response.set<ObjectNode>("Registration", json.string(truck.registration))
        response.set<ObjectNode>("Capacity", json.number(truck.capacity))
        response.set<ObjectNode>("Volume", json.number(truck.volume))
        response.set<ObjectNode>("Shipments", shipmentsNode)

        return response
    }
}
