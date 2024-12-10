package ru.yarsu.swg

import java.util.UUID

class SWGStorage(
    inputShipmentCertificatesList: MutableList<SWG>,
) {
    private val certificates: MutableMap<UUID, SWG> = mutableMapOf()

    init {
        inputShipmentCertificatesList.forEach { add(it) }
    }

    val size: Int
        get() {
            return getValues().size
        }

    fun getAll(): Map<UUID, SWG> = certificates

    fun getValues(): List<SWG> = certificates.values.toList()

    operator fun get(id: UUID): SWG? = certificates[id]

    fun add(shipmentCertificate: SWG) {
        require(!has(shipmentCertificate.id)) {
            "В хранилище уже есть акт с таким идентификатором ${shipmentCertificate.id}."
        }
        certificates[shipmentCertificate.id] = shipmentCertificate
    }

    fun has(id: UUID): Boolean = certificates.keys.any { it == id }
    operator fun set(id: UUID, value: SWG) {
        certificates[id] = value
    }
}
