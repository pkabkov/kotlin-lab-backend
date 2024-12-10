package ru.yarsu.console.commands

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWGStorage
import ru.yarsu.tools.FileHandler
import ru.yarsu.tools.Mapper
import ru.yarsu.tools.validateCSVFile
import ru.yarsu.tools.validatePort
import ru.yarsu.web.routes.applicationRoutes
import java.io.File
import kotlin.concurrent.thread

private val fileHandler = FileHandler()
private val mapper = Mapper()

@Parameters(separators = "=", commandDescription = "Start HTTP-server")
class Args {
    @Parameter(
        names = ["--swg-file"],
        description = "The triangles data file",
        required = true,
    )
    var dataFile: String? = null

    @Parameter(
        names = ["--employees-file"],
        description = "The users data file",
        required = true,
    )
    var usersFile: String? = null

    @Parameter(
        names = ["--dump-trucks-file"],
        description = "The dump trucks data file",
        required = true,
    )
    var trucksFile: String? = null

    @Parameter(
        names = ["--port"],
        description = "The port of HTTP-server",
        required = true,
    )
    var port: String? = null

    fun run() {
        validateCSVFile(dataFile)
        val swgFile = File(dataFile.orEmpty())
        validateCSVFile(usersFile)
        val usersFile = File(usersFile.orEmpty())
        validateCSVFile(trucksFile)
        val trucksFile = File(trucksFile.orEmpty())
        val port = port?.toInt()
        validatePort(port)

        val swgStorage = SWGStorage(fileHandler.readSWGDataCSV(swgFile))
        val employeeStorage = EmployeeStorage(fileHandler.readUsersDataCSV(usersFile))
        val truckStorage = DumpTruckStorage(fileHandler.readDumpTrucksDataCSV(trucksFile))

        val thread = thread(start = false) {
            fileHandler.saveSWG(swgStorage, swgFile)
            fileHandler.saveEmployees(employeeStorage, usersFile)
            fileHandler.saveDumpTrucks(truckStorage, trucksFile)
        }

        val app = applicationRoutes(swgStorage, employeeStorage, truckStorage, mapper)
        val server = port?.let { Netty(it) }?.let { app.asServer(it) }
        Runtime.getRuntime().addShutdownHook(thread)
        server?.start()
    }
}
