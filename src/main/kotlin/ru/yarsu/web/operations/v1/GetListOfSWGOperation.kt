package ru.yarsu.web.operations.v1

import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGStorage
import ru.yarsu.tools.OptionalParams
import ru.yarsu.web.operations.IGetHTTPEmployeesListOperation
import ru.yarsu.web.operations.cutListIntoPage

private const val PAGE_DEFAULT = 1
private const val RECORDS_PER_PAGE = 10

@Suppress("UNCHECKED_CAST")
class GetListOfSWGOperation(
    private val storage: SWGStorage,
) : IGetHTTPEmployeesListOperation {
    override fun <T> get(optionalParams: OptionalParams): List<T> {
        val page = optionalParams.page ?: PAGE_DEFAULT
        val recordsPerPage = optionalParams.recordsPerPage ?: RECORDS_PER_PAGE
        val certificates = storage.getValues()
        if (certificates.isEmpty()) {
            return emptyList<SWG>() as List<T>
        }

        val info =
            certificates
                .sortedWith(compareBy({ it.shipmentDateTime }, { it.id }))
                .map { certificate ->
                    mapOf(
                        "Id" to certificate.id,
                        "Title" to certificate.title,
                        "Cost" to certificate.cost,
                    )
                }

        return cutListIntoPage(page, recordsPerPage, info) as List<T>
    }
}
