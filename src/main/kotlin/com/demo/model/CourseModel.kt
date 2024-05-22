package com.demo.model

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import com.demo.entity.User

data class CourseModel(
    val id: Long? = null,
    val title: String,
    val description: String,
    val completed: Boolean,
    val authors: MutableList<User>,
)

fun CourseModel.toCourse(): Course {
    return Course(
        id = id,
        title = title,
        description = description,
        authors = authors,
        completed = completed,
    )
}

fun CourseModel.toCourseDTO(): CourseDTO {
    return CourseDTO(
        id = id,
        title = title,
        description = description,
        authors = getUsernames(authors),
        completed = completed,
    )
}
