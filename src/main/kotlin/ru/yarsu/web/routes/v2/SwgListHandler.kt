package ru.yarsu.web.routes.v2

import org.http4k.core.*
import org.http4k.format.Jackson.auto
import ru.yarsu.web.lenses.paginationLens
import ru.yarsu.web.operations.v2.SwgListOperation

class SwgListHandler(private val swgListOperation: SwgListOperation) : HttpHandler {
    private val swgLens = Body.auto<List<SwgListOperation.SwgResponse>>().toLens()

    override fun invoke(request: Request): Response {
        val pagination  = paginationLens(request)
        val info = swgListOperation.get(pagination.page, pagination.recordsPerPage)
        return Response(Status.OK).with(swgLens of info)
    }
}
