//package ru.yarsu.web.operations.v1
//
//import ru.yarsu.swg.SWG
//import ru.yarsu.swg.SWGStorage
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.web.operations.IGetHTTPEmployeesListOperation
//import ru.yarsu.web.operations.cutListIntoPage
//
//private const val PAGE_DEFAULT = 1
//private const val RECORDS_PER_PAGE = 10
//
//@Suppress("UNCHECKED_CAST")
//class GetListByTypeOperation(
//    private val storage: SWGStorage,
//) : IGetHTTPEmployeesListOperation {
//    override fun <T> get(optionalParams: OptionalParams): List<T> {
//        val swgTypeIn = optionalParams.swgType ?: throw IllegalArgumentException("Bad swgType")
//        val page = optionalParams.page ?: PAGE_DEFAULT
//        val recordsPerPage = optionalParams.recordsPerPage ?: RECORDS_PER_PAGE
//        val certificates = storage.getValues()
//        val info =
//            certificates
//                .filter { it.swgType == swgTypeIn }
//                .sortedWith(compareByDescending<SWG> { it.shipmentDateTime }.thenBy { it.id })
//                .map {
//                    mapOf(
//                        "Id" to it.id.toString(),
//                        "Title" to it.title,
//                        "Cost" to it.cost,
//                    )
//                }
//        if (info.isEmpty()) {
//            return emptyList()
//        }
//        return cutListIntoPage(page, recordsPerPage, info) as List<T>
//    }
//}
