package com.demo.dto

data class CourseDTO(
    val id: Long?,
    val title: String,
    val description: String,
    val authors: List<String>,
    val completed: Boolean,
)
