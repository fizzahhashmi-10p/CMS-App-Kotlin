package com.demo.dto

data class CourseEventDTO(
    val message: String,
    val course: CourseDTO
) : KafkaMessageDTO
