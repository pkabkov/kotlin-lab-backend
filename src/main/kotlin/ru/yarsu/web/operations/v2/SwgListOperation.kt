package ru.yarsu.web.operations.v2

import ru.yarsu.swg.SWGStorage
import ru.yarsu.web.operations.cutListIntoPage
import java.time.LocalDateTime
import java.util.*

@Suppress("UNCHECKED_CAST")
class SwgListOperation(
    private val swgStorage: SWGStorage,
) {
    companion object class SwgResponse(
        val id: UUID,
        val title: String,
        val cost: Double
    )

    fun get(page: Int, recordsPerPage: Int): List<SwgResponse> {
        val swgMap = swgStorage.getValues()
        if (swgMap.isEmpty()) {
            return emptyList()
        }
        val info =
            swgMap
                .sortedWith(compareBy({ it.shipmentDateTime }, { it.id }))
                .map {
                    SwgResponse(
                        id = it.id,
                        title = it.title,
                        cost = it.cost
                    )
                }

        return cutListIntoPage(page, recordsPerPage, info) as List<SwgResponse>
    }

}
