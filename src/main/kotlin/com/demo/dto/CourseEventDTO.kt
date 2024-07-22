package com.demo.dto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class CourseEventDTO(
    val message: String,
    val course: CourseDTO,
    val id: Long? = null
) : KafkaMessageDTO


fun serializeCourseEvent(courseEvent: CourseEventDTO): String {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.writeValueAsString(courseEvent)
}

fun deserializeCourseEvent(json: String): CourseEventDTO {
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(json)
}
