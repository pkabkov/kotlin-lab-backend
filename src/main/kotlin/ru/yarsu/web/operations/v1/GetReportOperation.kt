//package ru.yarsu.web.operations.v1
//
//import ru.yarsu.swg.SWG
//import ru.yarsu.swg.SWGMeasure
//import ru.yarsu.swg.SWGStorage
//import ru.yarsu.tools.OptionalParams
//import ru.yarsu.web.operations.IGetHTTPEmployeesListOperation
//import java.time.Month
//import java.time.format.TextStyle
//import java.util.Locale
//
//@Suppress("UNCHECKED_CAST")
//class GetReportOperation(
//    private val shipmentCertificateStorage: SWGStorage,
//) : IGetHTTPEmployeesListOperation {
//    override fun <T> get(optionalParams: OptionalParams): List<T> {
//        val year = optionalParams.year ?: 0
//        val certificates = shipmentCertificateStorage.getValues()
//
//        val list = certificates.filter { it.shipmentDateTime.year == year }
//        if (list.isEmpty()) {
//            return emptyList()
//        }
//
//        var certificatesByMonth: MutableMap<String, MutableList<SWG>> = mutableMapOf()
//        for (certificate in list) {
//            val numberOfMonth = certificate.shipmentDateTime.monthValue.toString()
//            if (!certificatesByMonth.containsKey(numberOfMonth)) {
//                certificatesByMonth[numberOfMonth] = mutableListOf(certificate)
//            } else {
//                certificatesByMonth[numberOfMonth]?.add(certificate)
//            }
//        }
//        certificatesByMonth = certificatesByMonth.toSortedMap(compareBy { it.toInt() })
//
//        val info =
//            certificatesByMonth
//                .map { certificate ->
//                    mapOf(
//                        "month" to certificate.key.toInt(),
//                        "monthTitle" to
//                            Month
//                                .of(certificate.key.toInt())
//                                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))
//                                .replaceFirstChar { it.uppercase() },
//                        "count" to certificate.value.size,
//                        "cost" to certificate.value.sumOf { it.cost },
//                        "weight" to
//                            certificate
//                                .value
//                                .filter { it.measure == SWGMeasure.T }
//                                .sumOf { it.count },
//                        "volume" to
//                            certificate
//                                .value
//                                .filter { it.measure == SWGMeasure.M3 }
//                                .sumOf { it.count },
//                    )
//                }
//
//        return info as List<T>
//    }
//}
