package ru.yarsu.web.routes.v2

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import ru.yarsu.web.operations.v2.GetListOfUsersOperation

class GetListOfUsersHandler(private val getListOfUsersOperation: GetListOfUsersOperation): HttpHandler {
    private val responseLens = Body.auto<ArrayNode>().toLens()
    override fun invoke(request: Request): Response {
        val response = getListOfUsersOperation.execute()
        return Response(Status.OK).with(responseLens of response)
    }
}
