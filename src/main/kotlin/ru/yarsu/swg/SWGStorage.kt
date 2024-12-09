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

    fun get(id: UUID): SWG? = certificates[id]

    fun add(shipmentCertificate: SWG) {
        require(!availabilityInStorageById(shipmentCertificate.id)) {
            "В хранилище уже есть акт с таким идентификатором ${shipmentCertificate.id}."
        }
        certificates[shipmentCertificate.id] = shipmentCertificate
    }

    fun availabilityInStorageById(id: UUID): Boolean = certificates.keys.any { it == id }
}
