package com.demo.entity

import com.demo.model.CourseModel
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
public data class Course(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,
    val title: String,
    val description: String,
    val author: String,
    val completed: Boolean = false
)

fun Course.toCourseModel(): CourseModel {
    return CourseModel(
        id = this.id,
        title = this.title,
        description = this.description,
        author = this.author,
        completed = this.completed
    )
}