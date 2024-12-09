package ru.yarsu.tools

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class Mapper {
    private val objectMapper = jacksonObjectMapper()

    init {
        objectMapper.registerModule(JavaTimeModule())
    }

    fun fromMapToJSONString(arg: Any): String =
        objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(arg)
}
