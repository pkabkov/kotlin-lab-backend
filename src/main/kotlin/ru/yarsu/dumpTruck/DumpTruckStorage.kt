package ru.yarsu.dumpTruck

import java.util.UUID

class DumpTruckStorage(
    trucksList: MutableList<DumpTruck>,
) {
    private var trucks: MutableMap<UUID, DumpTruck> = mutableMapOf()

    init {
        trucksList.forEach { add(it) }
    }

    fun add(truck: DumpTruck) {
        require(!has(truck.id)) {
            "В хранилище уже есть работник с идентификатором ${truck.id}."
        }
        trucks[truck.id] = truck
    }

    fun getValues(): List<DumpTruck>{
        return trucks.values.toList()
    }

    fun get(id: UUID): DumpTruck? = trucks[id]

    fun has(id: UUID): Boolean = trucks.keys.any { it == id }
}
