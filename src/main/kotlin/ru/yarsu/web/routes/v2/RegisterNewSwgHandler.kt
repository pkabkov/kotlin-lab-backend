package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.lens.*
import ru.yarsu.swg.SWGType

class RegisterNewSwgHandler : HttpHandler {
    private val responseLens = Body.auto<ObjectNode>().toLens()

    override fun invoke(request: Request): Response {

        val titleFiled = FormField.string().required("InvoiceTitle")
        val swgField = FormField.map { value ->
            if (!SWGType.entries.any { it.description == value }){
                throw IllegalArgumentException("Ожидается тип ПГС за списка")
            }
            SWGType.entries.find { it.description == value }
        }.required("InvoiceType")
        val weightField = FormField.map { value ->
            if (value.toDoubleOrNull() == null || value.toDouble() <= 0) {
                throw IllegalArgumentException("Ожидается положительное вещественное число")
            }
        }.required("InvoiceWeight")
        val priceField = FormField.map { value ->
            if (value.toDoubleOrNull() == null || value.toDouble() <= 0) {
                throw IllegalArgumentException("Ожидается положительное вещественное число")
            }
        }.required("InvoicePrice")
        val costField = FormField.map { value ->
            if (value.toDoubleOrNull() == null || value.toDouble() <= 0) {
                throw IllegalArgumentException("Ожидается положительное вещественное число")
            }
        }.optional("InvoiceCost")
        val dumpTruckModelField = FormField.string().required("DumpTruckModel")
        val dumpTruckRegistrationField = FormField.string().required("DumpTruckRegistration")

        val feedbackFormBody = Body.webForm(Validator.Feedback, titleFiled, swgField, weightField, priceField, costField).toLens()
        val form = feedbackFormBody(request)
        if (form.errors.isNotEmpty()){
            form.fields.forEach {
                println(it.key + " " + it.value)
            }
        }
//        val json = Jackson
//        val jsonFactory = JsonNodeFactory.instance
//
//        val errors = jsonFactory.objectNode()
//
//        println(request.bodyString())
//        val requestJson = request.bodyString().asJsonObject()
//        val titleField = requestJson["InvoiceTitle"]
//        val swgField = requestJson["InvoiceType"]
//        val weightField = requestJson["InvoiceWeight"]
//        val priceField = requestJson["InvoicePrice"]
//        val costField = requestJson["InvoiceCost"]
//        val dumpTruckModelField = requestJson["DumpTruckModel"]
//        val dumpTruckRegistrationField = requestJson["DumpTruckRegistration"]
//
//        if (titleField == null) {
//            errors.set<ObjectNode>(
//                "Title", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Поле Title отсуствует")
//                )
//            )
//        } else if (!titleField.isTextual) {
//            errors.set<ObjectNode>(
//                "Title", json.obj(
//                    "Value" to titleField,
//                    "Error" to json.string("Ожидается строка")
//                )
//            )
//        }
//
//        if (swgField == null) {
//            errors.set<ObjectNode>(
//                "SWG", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Поле SWG отсуствует")
//                )
//            )
//        } else {
//            if (SWGType.entries.find { it.description == swgField.asText() } == null) {
//                errors.set<ObjectNode>(
//                    "SWG", json.obj(
//                        "Value" to swgField,
//                        "Error" to json.string("Ожидается тип ПГС за списка")
//                    )
//                )
//            }
//        }
//
//        if (weightField == null) {
//            errors.set<ObjectNode>(
//                "Weight", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Отсутствует поле Weight")
//                )
//            )
//        } else if (!weightField.isNumber || weightField.asDouble() <= 0) {
//            errors.set<ObjectNode>(
//                "Weight", json.obj(
//                    "Value" to weightField,
//                    "Error" to json.string("Ожидается положительное вещественное число")
//                )
//            )
//        }
//
//        if (priceField == null) {
//            errors.set<ObjectNode>(
//                "Price", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Отсутствует поле Price")
//                )
//            )
//        } else if (!priceField.isNumber || priceField.asDouble() <= 0) {
//            errors.set<ObjectNode>(
//                "Price", json.obj(
//                    "Value" to priceField,
//                    "Error" to json.string("Ожидается положительное вещественное число")
//                )
//            )
//        }
//
//        if (costField != null && (!costField.isNumber || costField.asDouble() <= 0)) {
//            errors.set<ObjectNode>(
//                "Cost", json.obj(
//                    "Value" to costField,
//                    "Error" to json.string("Ожидается положительное вещественное число")
//                )
//            )
//        }
//
//        if (dumpTruckModelField == null) {
//            errors.set<ObjectNode>(
//                "DumpTruckModel", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Поле DumpTruckModel отсуствует")
//                )
//            )
//        } else if (!dumpTruckModelField.isTextual) {
//            errors.set<ObjectNode>(
//                "DumpTruckModel", json.obj(
//                    "Value" to dumpTruckModelField,
//                    "Error" to json.string("Ожидается строка")
//                )
//            )
//        }
//
//        if (dumpTruckRegistrationField == null) {
//            errors.set<ObjectNode>(
//                "DumpTruckRegistration", json.obj(
//                    "Value" to json.nullNode(),
//                    "Error" to json.string("Поле DumpTruckRegistration отсуствует")
//                )
//            )
//        } else if (!dumpTruckRegistrationField.isTextual) {
//            errors.set<ObjectNode>(
//                "DumpTruckRegistration", json.obj(
//                    "Value" to dumpTruckRegistrationField,
//                    "Error" to json.string("Ожидается строка")
//                )
//            )
//        }
//
//        if (!errors.isEmpty) {
//            return Response(Status.BAD_REQUEST).with(responseLens of errors)
//        }

        return Response(Status.OK)
    }
}
