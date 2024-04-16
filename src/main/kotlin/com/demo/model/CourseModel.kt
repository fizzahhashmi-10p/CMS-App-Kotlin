package com.demo.model

import com.demo.dto.CourseDTO
import com.demo.entity.Course

data class CourseModel (
    val id: Long? = null,
    val title: String,
    val description: String,
    val author: String,
    val completed: Boolean
)

fun CourseModel.toCourse(): Course {
    return Course(
        id = this.id,
        title = this.title,
        description = this.description,
        author = this.author,
        completed = this.completed
    )
}

fun CourseModel.toCourseDTO(): CourseDTO {
    return CourseDTO(
        title = this.title,
        description = this.description,
        author = this.author,
        completed = this.completed
    )
}