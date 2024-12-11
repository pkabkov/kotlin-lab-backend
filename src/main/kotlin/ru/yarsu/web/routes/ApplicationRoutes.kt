package ru.yarsu.web.routes

import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.then
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.dumpTruck.DumpTruckStorage
import ru.yarsu.employee.EmployeeStorage
import ru.yarsu.swg.SWGStorage
import ru.yarsu.tools.Mapper
import ru.yarsu.web.operations.PingHandler
import ru.yarsu.web.operations.v1.GetListByPeriodOperation
import ru.yarsu.web.operations.v1.GetListByTypeOperation
import ru.yarsu.web.operations.v1.GetReportOperation
import ru.yarsu.web.operations.v2.*
import ru.yarsu.web.routes.v1.GetListByPeriodHandler
import ru.yarsu.web.routes.v1.GetListByTypeHandler
import ru.yarsu.web.routes.v1.GetReportHandler
import ru.yarsu.web.routes.v2.*

fun applicationRoutes(
    swgStorage: SWGStorage,
    usersStorage: EmployeeStorage,
    dumpTruckStorage: DumpTruckStorage,
    mapper: Mapper,
): RoutingHttpHandler {

    val pingHandler = PingHandler()
    val swgListHandler = SwgListHandler(SwgListOperation(swgStorage))
    val newSwgHandler =
        NewSwgHandler(NewSwgOperation(swgStorage, dumpTruckStorage, usersStorage), dumpTruckStorage, usersStorage)
    val swgInfoHandler = SwgInfoHandler(swgStorage)
    val replaceSwgHandler = ReplaceSwgHandler(
        ReplaceSwgOperation(swgStorage, dumpTruckStorage, usersStorage),
        swgStorage,
        dumpTruckStorage,
        usersStorage
    )
    val getListByTypeHandler = GetListByTypeHandler(GetListByTypeOperation(swgStorage), mapper)
    val getListByPeriodHandler = GetListByPeriodHandler(GetListByPeriodOperation(swgStorage), mapper)
    val getReportHandler = GetReportHandler(GetReportOperation(swgStorage), mapper)
    val dumpTruckInfoHandler = DumpTruckInfoHandler(dumpTruckStorage, DumpTruckInfoOperation(dumpTruckStorage, swgStorage))
    val dumpTruckDeleteHandler = DumpTruckDeleteHandler(swgStorage, dumpTruckStorage)
    val getListOfUsersHandler = GetListOfUsersHandler(GetListOfUsersOperation(usersStorage))
    val registerNewSwgHandler = RegisterNewSwgHandler()

    return routes(
        "/ping" bind GET to pingHandler,
        "/v2/shipments/by-type" bind GET to getListByTypeHandler,
        "/v2/shipments/by-period" bind GET to getListByPeriodHandler,
        "/v2/shipments/report" bind GET to getReportHandler,
        "/v2/dump-trucks/{dump-truck-id}" bind GET to handlerFilter().then(dumpTruckInfoHandler),
        "/v2/dump-trucks/" bind GET to handlerFilter().then(dumpTruckInfoHandler),
        "/v2/dump-trucks/{dump-truck-id}" bind Method.DELETE to handlerFilter().then(dumpTruckDeleteHandler),
        "/v2/dump-trucks/" bind Method.DELETE to handlerFilter().then(dumpTruckDeleteHandler),
        "/v2/employees" bind GET to handlerFilter().then(getListOfUsersHandler),
        "/v2/employees/{employee-id}/register-invoice" bind Method.POST to handlerFilter().then(registerNewSwgHandler)

//        "/v2/shipments" bind GET to handlerFilter().then(swgListHandler),
//        "/v2/shipments" bind Method.POST to handlerFilter().then(newSwgHandler),
//        "/v2/shipments/{shipment-id}" bind GET to handlerFilter().then(swgInfoHandler),
//        "/v2/shipments/" bind GET to handlerFilter().then(swgInfoHandler),
//        "/v2/shipments/{shipment-id}" bind Method.PUT to handlerFilter().then(replaceSwgHandler)
    )
}
