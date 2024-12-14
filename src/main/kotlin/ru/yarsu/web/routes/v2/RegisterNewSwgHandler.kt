package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.core.body.formAsMap
import org.http4k.format.Jackson
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.lens.*
import ru.yarsu.dumpTruck.DumpTruck
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGMeasure
import ru.yarsu.swg.SWGStorage
import ru.yarsu.swg.SWGType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterNewSwgHandler(
    private val swgStorage: SWGStorage,
    private val dumpTruckStorage: DumpTruckStorage,
    private val employeeStorage: EmployeeStorage
) : HttpHandler {
    private val responseLens = Body.auto<ObjectNode>().toLens()
    private val listLens = Body.auto<ArrayNode>().toLens()
    private val json = Jackson
    private val jsonFactory = JsonNodeFactory.instance

    override fun invoke(request: Request): Response {

        val idLens = Path.uuid().of("employee-id")
        try {
            val id = idLens(request)
        } catch (e: Exception) {
            val errorNode = jsonFactory.objectNode()
            errorNode.set<ObjectNode>(
                "Error",
                json.string("Некорректное значение переданного параметра id. Ожидается UUID, но получено текстовое значение")
            )
            return Response(Status.BAD_REQUEST).with(responseLens of errorNode)
        }

        val managerId = idLens(request)

        val titleFiled = FormField.nonBlankString().required("InvoiceTitle")
        val swgField = FormField.mapWithNewMeta(nextIn = { value ->
            val type = SWGType.entries.find { it.description == value } ?: throw IllegalArgumentException()
            type
        }, nextOut = { it.description }, paramMeta = ParamMeta.ObjectParam).required("InvoiceType")
        val weightField = FormField.double().map { value ->
            if (value <= 0) {
                throw IllegalArgumentException()
            }
            value
        }.required("InvoiceWeight")
        val priceField = FormField.double().map { value ->
            if (value <= 0) {
                throw IllegalArgumentException()
            }
            value
        }.required("InvoicePrice")
        val costField = FormField.double().map { value ->
            if (value <= 0) {
                throw IllegalArgumentException()
            }
            value
        }.optional("InvoiceCost")
        val dumpTruckModelField = FormField.nonBlankString().required("DumpTruckModel")
        val dumpTruckRegistrationField = FormField.nonBlankString().required("DumpTruckRegistration")

        val feedbackFormBody = Body.webForm(
            Validator.Feedback,
            titleFiled,
            swgField,
            weightField,
            priceField,
            costField,
            dumpTruckModelField,
            dumpTruckRegistrationField
        ).toLens()

        val form = feedbackFormBody(request)
        val errors = jsonFactory.objectNode()
        if (form.errors.isNotEmpty()) {
            for (it in form.errors) {
                val valueList = form.fields[it.meta.name]
                if (valueList == null) {
                    errors.set<ObjectNode>(
                        it.meta.name, json.obj(
                            "Value" to json.nullNode(),
                            "Error" to json.string("Отсутствует поле ${it.meta.name}")
                        )
                    )
                    continue
                }

                val value = valueList[0]

                if (value.isBlank()) {
                    errors.set<ObjectNode>(
                        it.meta.name, json.obj(
                            "Value" to json.nullNode(),
                            "Error" to json.string("Отсутствует поле ${it.meta.name}")
                        )
                    )
                    continue
                }

                if (it.meta.paramMeta.description == "number") {
                    errors.set<ObjectNode>(
                        it.meta.name, json.obj(
                            "Value" to json.string(value),
                            "Error" to json.string("Ожидается положительное вещественное число")
                        )
                    )
                    continue
                }

                if (it.meta.paramMeta.description == "object") {
                    errors.set<ObjectNode>(
                        it.meta.name, json.obj(
                            "Value" to json.string(value),
                            "Error" to json.string("Ожидается тип из ПГС списка")
                        )
                    )
                    continue
                }

                errors.set<ObjectNode>(
                    it.meta.name, json.obj(
                        "Value" to json.string(value),
                        "Error" to json.string("Ожидается строка")
                    )
                )
            }

            return Response(Status.BAD_REQUEST).with(responseLens of errors)
        }

        if (!employeeStorage.has(managerId)) {
            val errorNode = jsonFactory.objectNode()
            errorNode.set<ObjectNode>("EmployeeId", json.string(managerId.toString()))
            errorNode.set<ObjectNode>("Error", json.string("Работник не найден"))
            return Response(Status.NOT_FOUND).with(responseLens of errorNode)
        }

        val title = titleFiled(form)
        val swgType = swgField(form)
        val weight = weightField(form)
        val price = priceField(form)
        val cost = costField(form) ?: (price * weight)
        val dumpTruckModel = dumpTruckModelField(form)
        val dumpTruckRegistration = dumpTruckRegistrationField(form)


        val truckList = dumpTruckStorage.getValues()
            .filter { it.registration == dumpTruckRegistration && it.model == dumpTruckModel }
        if (truckList.count() > 1) {
            val errorNode = jsonFactory.arrayNode()
            truckList
                .sortedWith(compareBy<DumpTruck> { it.capacity }.thenBy { it.id })
                .forEach { truck ->
                    val tmp = jsonFactory.objectNode()
                    tmp.set<ObjectNode>("Id", json.string(truck.id.toString()))
                    tmp.set<ObjectNode>("Capacity", json.number(truck.capacity))
                    tmp.set<ObjectNode>("Volume", json.number(truck.volume))
                    tmp.set<ObjectNode>(
                        "ShipmentsCount", json.number(
                            swgStorage.getValues().count { it.dumpTruck == truck.id }
                        )
                    )
                    errorNode.add(tmp)
                }
            return Response(Status.CONFLICT).with(listLens of errorNode)
        }

        val truck: DumpTruck
        if (truckList.isEmpty()) {
            truck = DumpTruck(
                id = UUID.randomUUID(),
                model = dumpTruckModel,
                registration = dumpTruckRegistration,
                capacity = weight,
                volume = weight / swgType.density,
            )
        } else {
            truck = truckList[0]
            if (truck.capacity < weight) {
                val errorNode = jsonFactory.objectNode()
                errorNode.set<ObjectNode>(
                    "Error",
                    json.string("Вес отгрузки превышает грузоподъёмность кузова самосвала")
                )
                return Response(Status.FORBIDDEN).with(responseLens of errorNode)
            }
        }

        val swg = SWG(
            id = UUID.randomUUID(),
            title = title,
            swgType = swgType,
            measure = SWGMeasure.T,
            count = weight,
            price = price,
            cost = cost,
            shipmentDateTime = LocalDateTime.parse(
                LocalDateTime.now().toString(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ),
            dumpTruck = truck.id,
            washing = false,
            manager = managerId,
        )

        swgStorage.add(swg)
        val response = jsonFactory.objectNode()
        response.set<ObjectNode>("ShipmentId", json.string(swg.id.toString()))
        response.set<ObjectNode>("DumpTruckId", json.string(truck.id.toString()))
        return Response(Status.CREATED).with(responseLens of response)
    }
}
