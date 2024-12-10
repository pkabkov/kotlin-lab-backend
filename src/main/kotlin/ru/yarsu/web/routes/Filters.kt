package ru.yarsu.web.routes

import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.*

private val errorsLens = Body.auto<Map<String, String>>().toLens()

private fun lensFailureFilter(): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            try {
                next(request)
            } catch (lensFailure: LensFailure) {
                Response(Status.BAD_REQUEST).with(errorsLens of mapOf("Error" to lensFailure.message.orEmpty()))
            }
        }
    }

private val jsonContentTypeFilter = Filter { next: HttpHandler ->
    { request ->
        val response = next(request)
        if (response.body.payload.remaining() > 0) {
            response.contentType(ContentType.APPLICATION_JSON)
        } else {
            response
        }
    }
}

fun handlerFilter(): Filter {
    return lensFailureFilter()
        .then(jsonContentTypeFilter)
}

fun lensRequiredFilter(): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            try {
                next(request)
            } catch (lensFailure: LensFailure) {
                Response(Status.BAD_REQUEST).with(errorsLens of mapOf("Error" to lensFailure.message.orEmpty()))
            }
        }
    }

