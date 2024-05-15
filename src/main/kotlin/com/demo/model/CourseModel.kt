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
        id = this.id,
        title = this.title,
        description = this.description,
        authors = this.authors,
        completed = this.completed,
    )
}

fun CourseModel.toCourseDTO(): CourseDTO {
    return CourseDTO(
        id = this.id,
        title = this.title,
        description = this.description,
        authors = getUsernames(this.authors),
        completed = this.completed,
    )
}
