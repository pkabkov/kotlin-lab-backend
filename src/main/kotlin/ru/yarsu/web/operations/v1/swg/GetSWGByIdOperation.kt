//package ru.yarsu.web.operations.v1.swg
//
//import ru.yarsu.employee.EmployeeStorage
//import ru.yarsu.swg.SWGStorage
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.web.operations.IGetHTTPEmployeesMapOperation
//
//@Suppress("UNCHECKED_CAST")
//class GetSWGByIdOperation(
//    private val swgStorage: SWGStorage,
//    private val usersStorage: EmployeeStorage,
//) : IGetHTTPEmployeesMapOperation {
//    override fun <T> get(optionalParams: OptionalParams): Map<String, T> {
//        val id = optionalParams.id
//        val certificateOrNull = id?.let { swgStorage.get(id) }
//        val certificate =
//            certificateOrNull?.let {
//                mapOf(
//                    "Id" to it.id.toString(),
//                    "Title" to it.title,
//                    "SWG" to it.swgType.description,
//                    "Measure" to it.measure.description,
//                    "Count" to it.count,
//                    "Price" to it.price,
//                    "Cost" to it.cost,
//                    "ShipmentDateTime" to it.shipmentDateTime.toString(),
//                    "Model" to it.model,
//                    "Registration" to it.registration,
//                    "Washing" to it.washing,
//                    "ManagerEmail" to usersStorage.get(it.manager)?.email,
//                )
//            } ?: emptyMap()
//        return certificate as Map<String, T>
//    }
//}
