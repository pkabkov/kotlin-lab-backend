package ru.yarsu.web.operations.v2

import org.http4k.lens.LensFailure
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGMeasure
import ru.yarsu.swg.SWGStorage
import ru.yarsu.swg.SWGType
import java.time.LocalDateTime
import java.util.*

class NewSwgOperation(
    private val swgStorage: SWGStorage,
    private val dumpTruckStorage: DumpTruckStorage,
    private val employeeStorage: EmployeeStorage
) {
    fun execute(
        title: String,
        swg: SWGType,
        measure: SWGMeasure,
        count: Double,
        price: Double,
        cost: Double,
        shipmentDateTime: LocalDateTime,
        washing: Boolean,
        dumpTruck: UUID,
        manager: UUID,
    ): UUID {

        if (measure == SWGMeasure.M3 &&
            count > dumpTruckStorage[dumpTruck]!!.volume)
        {
            throw IllegalArgumentException("Объём отгрузки превышает объём кузова самосвала")
        }
        else if (count > dumpTruckStorage[dumpTruck]!!.capacity) {
            throw IllegalArgumentException("Масса отгрузки превышает грузоподъемность самосвала")
        }

        if (shipmentDateTime < employeeStorage[manager]!!.registrationDateTime) {
            throw IllegalArgumentException("Отгрузка произведена до регистрации работника")
        }

        val newSwg =
            SWG(
                id = UUID.randomUUID(),
                title = title,
                swgType = swg,
                measure = measure,
                count = count,
                price = price,
                cost = cost,
                shipmentDateTime = shipmentDateTime,
                washing = washing,
                dumpTruck = dumpTruck,
                manager = manager
            )

        swgStorage.add(newSwg)
        return newSwg.id
    }
}
