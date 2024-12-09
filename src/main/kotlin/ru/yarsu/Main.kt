package ru.yarsu

import com.beust.jcommander.JCommander
import ru.yarsu.console.commands.Args
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val command = Args()

    val commander: JCommander =
        JCommander
            .newBuilder()
            .addObject(command)
            .build()

    try {
//        commander.parse(*args)
        commander.parse("--swg-file","data.csv", "--dump-trucks-file", "trucks.csv", "--employees-file", "employees.csv", "--port", "9000")
        command.run()
    } catch (e: Exception) {
        System.err.println(e.message)
        commander.usage()
        exitProcess(1)
    }
}
