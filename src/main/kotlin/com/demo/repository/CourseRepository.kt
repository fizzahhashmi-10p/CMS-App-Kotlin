package com.demo.repository

import com.demo.model.Course
import org.springframework.data.jpa.repository.JpaRepository

public interface CourseRepository : JpaRepository<Course, Long>{
    fun findByAuthor(author: String):List<Course>
}
