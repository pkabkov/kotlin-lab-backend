package ru.yarsu.tools

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import ru.yarsu.dumpTruck.DumpTruck
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.Employee
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWG
import ru.yarsu.swg.SWGMeasure
import ru.yarsu.swg.SWGStorage
import ru.yarsu.swg.SWGType
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

class FileHandler {
    fun readSWGDataCSV(file: File): MutableList<SWG> {
        try {
            csvReader().readAllWithHeader(file)
        } catch (e: CSVFieldNumDifferentException) {
            return mutableListOf()
        }

        val certificates = mutableListOf<SWG>()
        csvReader().readAllWithHeader(file).forEach {
            val id = UUID.fromString(it["Id"])
            val title = it["Title"].toString()
            val swgType = SWGType.valueOf(it["SWG"].orEmpty())
            val measure = SWGMeasure.valueOf(it["Measure"].orEmpty())
            val count = it["Count"].orEmpty().toDouble()
            val price = it["Price"].orEmpty().toDouble()
            val cost = it["Cost"].orEmpty().toDouble()
            val shipmentDateTime =
                LocalDateTime.parse(it["ShipmentDateTime"].orEmpty().trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val dumpTruck = UUID.fromString(it["DumpTruck"])
            val washing = it["Washing"].toBoolean()
            val manager = UUID.fromString(it["Manager"])

            certificates.add(
                SWG(
                    id = id,
                    title = title,
                    swgType = swgType,
                    measure = measure,
                    count = count,
                    price = price,
                    cost = cost,
                    shipmentDateTime = shipmentDateTime,
                    dumpTruck = dumpTruck,
                    washing = washing,
                    manager = manager,
                ),
            )
        }

        return certificates
    }

    fun readUsersDataCSV(file: File): MutableList<Employee> {
        try {
            csvReader().readAllWithHeader(file)
        } catch (e: CSVFieldNumDifferentException) {
            return mutableListOf()
        }

        val usersList = mutableListOf<Employee>()
        csvReader().readAllWithHeader(file).forEach { row ->
            val id = UUID.fromString(row["Id"])
            val name = row["Name"].orEmpty()
            val position = row["Position"].orEmpty()
            val registrationDateTime = LocalDateTime.parse(row["RegistrationDateTime"].orEmpty())
            val email = row["Email"].orEmpty()

            usersList
                .add(
                    Employee(
                        id = id,
                        name = name,
                        position = position,
                        registrationDateTime = registrationDateTime,
                        email = email,
                    ),
                )
        }

        return usersList
    }

    fun readDumpTrucksDataCSV(file: File): MutableList<DumpTruck> {
        try {
            csvReader().readAllWithHeader(file)
        } catch (e: CSVFieldNumDifferentException) {
            return mutableListOf()
        }

        val trucksList = mutableListOf<DumpTruck>()
        csvReader().readAllWithHeader(file).forEach { row ->
            val id = UUID.fromString(row["Id"])
            val model = row["Model"].orEmpty()
            val registration = row["Registration"].orEmpty()
            val capacity = row["Capacity"].orEmpty().toDouble()
            val volume = row["Volume"].orEmpty().toDouble()

            trucksList
                .add(
                    DumpTruck(
                        id = id,
                        model = model,
                        registration = registration,
                        capacity = capacity,
                        volume = volume
                    ),
                )
        }

        return trucksList
    }

    fun saveDumpTrucks(dumpTruckStorage: DumpTruckStorage, file: File){
        csvWriter().open(file) {
                writeRow(listOf("Id","Model","Registration","Capacity","Volume"))
                dumpTruckStorage.getValues().forEach {
                    writeRow(listOf(it.id, it.model, it.registration, it.capacity, it.volume))
                }
            }
    }

    fun saveSWG(swgStorage: SWGStorage, file: File){
        csvWriter().open(file) {
            writeRow(listOf("Id","Title","SWG","Measure","Count","Price","Cost","ShipmentDateTime","DumpTruck","Washing","Manager"))
            swgStorage.getValues().forEach {
                writeRow(listOf(it.id, it.title, it.swgType, it.measure, it.count, it.price, it.cost, it.shipmentDateTime, it.dumpTruck, it.washing, it.manager))
            }
        }
    }

    fun saveEmployees(employeeStorage: EmployeeStorage, file: File){
        csvWriter().open(file) {
            writeRow(listOf("Id","Name","Position","RegistrationDateTime","Email"))
            employeeStorage.getValues().forEach {
                writeRow(listOf(it.id, it.name, it.position, it.registrationDateTime, it.email))
            }
        }
    }
}
