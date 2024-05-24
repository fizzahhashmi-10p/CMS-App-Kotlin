package com.demo.dto

data class CourseDTO(
    val id: Long?,
    var title: String,
    val description: String,
    var authors: List<String>,
    val completed: Boolean,
)
