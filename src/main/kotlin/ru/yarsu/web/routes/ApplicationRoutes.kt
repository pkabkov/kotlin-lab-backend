package ru.yarsu.web.routes

import org.http4k.core.Method.GET
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWGStorage
import ru.yarsu.tools.Mapper
import ru.yarsu.web.operations.PingHandler

fun applicationRoutes(
    swgStorage: SWGStorage,
    usersStorage: EmployeeStorage,
    mapper: Mapper,
): RoutingHttpHandler {
    // '/'
    val pingHandler = PingHandler()

    return routes(
        "/ping" bind GET to pingHandler,
    )
}
