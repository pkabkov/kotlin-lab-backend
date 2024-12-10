package ru.yarsu.web.operations.v1

import ru.yarsu.swg.SWGStorage
import ru.yarsu.tools.OptionalParams
import ru.yarsu.web.operations.IGetHTTPEmployeesListOperation
import ru.yarsu.web.operations.cutListIntoPage
import java.time.LocalDate

private const val PAGE_DEFAULT = 1
private const val RECORDS_PER_PAGE = 10

@Suppress("UNCHECKED_CAST")
class GetListByPeriodOperation(
    private val shipmentCertificateStorage: SWGStorage,
) : IGetHTTPEmployeesListOperation {
    override fun <T> get(optionalParams: OptionalParams): List<T> {
        val start = optionalParams.startTime ?: LocalDate.now()
        val end = optionalParams.endTime ?: LocalDate.now()
        val page = optionalParams.page ?: PAGE_DEFAULT
        val recordsPerPage = optionalParams.recordsPerPage ?: RECORDS_PER_PAGE
        val certificates = shipmentCertificateStorage.getValues()

        val map =
            certificates
                .filter { it.shipmentDateTime.toLocalDate() in start..end }
                .sortedWith(compareBy({ it.shipmentDateTime }, { it.id }))
        if (map.isEmpty()) {
            return emptyList()
        }
        val info =
            map
                .map {
                    mapOf(
                        "Id" to it.id.toString(),
                        "Title" to it.title,
                        "ShipmentDate" to it.shipmentDateTime.toLocalDate().toString(),
                        "Cost" to it.cost,
                    )
                }
        return cutListIntoPage(page, recordsPerPage, info) as List<T>
    }
}
