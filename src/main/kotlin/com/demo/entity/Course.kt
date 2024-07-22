package com.demo.entity

import com.demo.model.CourseModel
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.FetchType

@Entity
@Table(name = "courses")
public data class Course(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,
    var title: String,
    var description: String,
    var completed: Boolean = false,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "course_author",
        joinColumns = [JoinColumn(name = "course_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    var authors: MutableList<User> = mutableListOf(),
)

fun Course.toCourseModel(): CourseModel {
    return CourseModel(
        id = id,
        title = title,
        description = description,
        authors = authors,
        completed = completed,
    )
}
