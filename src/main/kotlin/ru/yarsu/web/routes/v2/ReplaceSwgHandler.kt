package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWGMeasure
import ru.yarsu.swg.SWGStorage
import ru.yarsu.swg.SWGType
import ru.yarsu.web.operations.v2.NewSwgOperation
import ru.yarsu.web.operations.v2.ReplaceSwgOperation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class ReplaceSwgHandler(
    private val operation: ReplaceSwgOperation,
    private val swgStorage: SWGStorage,
    private val dumpTruckStorage: DumpTruckStorage,
    private val employeeStorage: EmployeeStorage
) : HttpHandler {

    private val responseLens = Body.auto<ObjectNode>().toLens()
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

        val json = Jackson
        val jsonFactory = JsonNodeFactory.instance

        val errors = jsonFactory.objectNode()

        val bodyString = request.bodyString()
        try {
            bodyString.asJsonObject()
        } catch (e: Exception) {
            return Response(Status.BAD_REQUEST).with(responseLens of jsonFactory.objectNode().apply
            {
                put("Value", bodyString)
                put("Error", "Missing a name for object member.")
            })
        }
        val requestJson = bodyString.asJsonObject()
        val titleField = requestJson["Title"]
        val swgField = requestJson["SWG"]
        val measureField = requestJson["Measure"]
        val countField = requestJson["Count"]
        val priceField = requestJson["Price"]
        val costField = requestJson["Cost"]
        val shipmentDateTimeField = requestJson["ShipmentDateTime"]
        val washingField = requestJson["Washing"]
        val dumpTruckField = requestJson["DumpTruck"]
        val managerField = requestJson["Manager"]

        if (titleField == null) {
            errors.set<ObjectNode>(
                "Title", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Поле Title отсуствует")
                )
            )
        } else if (!titleField.isTextual) {
            errors.set<ObjectNode>(
                "Title", json.obj(
                    "Value" to titleField,
                    "Error" to json.string("Ожидается строка")
                )
            )
        }

        if (swgField == null) {
            errors.set<ObjectNode>(
                "SWG", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Поле SWG отсуствует")
                )
            )
        } else {
            if (SWGType.entries.find { it.description == swgField.asText() } == null) {
                errors.set<ObjectNode>(
                    "SWG", json.obj(
                        "Value" to swgField,
                        "Error" to json.string("Ожидается тип ПГС за списка")
                    )
                )
            }
        }

        if (measureField == null) {
            errors.set<ObjectNode>(
                "Measure", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Поле Measure отсутствует")
                )
            )
        } else {
            if (SWGMeasure.entries.find { it.description == measureField.asText() } == null) {
                errors.set<ObjectNode>(
                    "Measure", json.obj(
                        "Value" to measureField,
                        "Error" to json.string("Ожидается единица измерения за списка")
                    )
                )
            }
        }

        if (countField == null) {
            errors.set<ObjectNode>(
                "Count", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Отсутствует поле Count")
                )
            )
        } else if (!countField.isNumber || countField.asDouble() <= 0) {
            errors.set<ObjectNode>(
                "Count", json.obj(
                    "Value" to countField,
                    "Error" to json.string("Ожидается положительное вещественное число")
                )
            )
        }

        if (priceField == null) {
            errors.set<ObjectNode>(
                "Price", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Отсутствует поле Price")
                )
            )
        } else if (!priceField.isNumber || priceField.asDouble() <= 0) {
            errors.set<ObjectNode>(
                "Price", json.obj(
                    "Value" to priceField,
                    "Error" to json.string("Ожидается положительное вещественное число")
                )
            )
        }

        if (costField == null) {
            errors.set<ObjectNode>(
                "Cost", json.obj(
                    "Value" to json.nullNode(),
                    "Error" to json.string("Отсутствует поле Cost")
                )
            )
        } else if (!costField.isNumber || costField.asDouble() <= 0) {
            errors.set<ObjectNode>(
                "Cost", json.obj(
                    "Value" to costField,
                    "Error" to json.string("Ожидается положительное вещественное число")
                )
            )
        }

        if (shipmentDateTimeField != null && shipmentDateTimeField.isTextual) {
            try {
                LocalDateTime.parse(shipmentDateTimeField.asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e: DateTimeParseException) {
                errors.set<ObjectNode>(
                    "ShipmentDateTime", json.obj(
                        "Value" to shipmentDateTimeField,
                        "Error" to json.string("Ожидается дата и время")
                    )
                )
            }
        }

        if (washingField != null && !washingField.isBoolean) {
            errors.set<ObjectNode>(
                "Washing", json.obj(
                    "Value" to washingField,
                    "Error" to json.string("Ожидается булевское значение")
                )
            )
        }

        if (dumpTruckField == null || !dumpTruckField.isTextual) {
            errors.set<ObjectNode>(
                "DumpTruckField", json.obj(
                    "Value" to dumpTruckField,
                    "Error" to json.string("Ожидается корректный UUID")
                )
            )
        } else {
            try {
                val id = UUID.fromString(dumpTruckField.asText())
                if (!dumpTruckStorage.has(id)) {
                    throw IllegalArgumentException()
                }
            } catch (e: IllegalArgumentException) {
                errors.set<ObjectNode>(
                    "DumpTruckField", json.obj(
                        "Value" to dumpTruckField,
                        "Error" to json.string("Ожидается корректный UUID")
                    )
                )
            }
        }

        if (managerField == null || !managerField.isTextual) {
            errors.set<ObjectNode>(
                "Manager", json.obj(
                    "Value" to managerField,
                    "Error" to json.string("Ожидается корректный UUID")
                )
            )
        } else {
            try {
                val id = UUID.fromString(managerField.asText())
                if (!employeeStorage.has(id)) {
                    throw IllegalArgumentException()
                }
            } catch (e: IllegalArgumentException) {
                errors.set<ObjectNode>(
                    "Manager", json.obj(
                        "Value" to managerField,
                        "Error" to json.string("Ожидается корректный UUID")
                    )
                )
            }
        }

        if (!errors.isEmpty) {
            return Response(Status.BAD_REQUEST).with(responseLens of errors)
        }

        val title = titleField.asText()
        val swg = SWGType.entries.find { it.description == swgField.asText() }!!
        val measure = SWGMeasure.entries.find { it.description == measureField.asText() }!!
        val count = countField.asDouble()
        val price = priceField.asDouble()
        val cost = costField.asDouble()
        val shipmentDateTime = shipmentDateTimeField?.let {
            LocalDateTime.parse(it.asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } ?: LocalDateTime.parse(LocalDateTime.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val washing = washingField?.asBoolean() ?: false
        val dumpTruck = UUID.fromString(dumpTruckField.asText())
        val manager = UUID.fromString(managerField.asText())

        try {
            operation.execute(
                id,
                title,
                swg,
                measure,
                count,
                price,
                cost,
                shipmentDateTime,
                washing,
                dumpTruck,
                manager
            )

        } catch (e: IllegalArgumentException) {
            return Response(Status.FORBIDDEN).with(responseLens of jsonFactory.objectNode().apply
            {
                put("Error", e.message)
            })
        }

        return Response(Status.NO_CONTENT)
    }
}

