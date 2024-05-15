package com.demo.repository

import com.demo.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface CourseRepository : JpaRepository<Course, Long> {
    @Query(
        value =
            "SELECT c.* FROM courses c " +
                "JOIN course_author ca ON (ca.course_id = c.id) " +
                "JOIN users u ON (u.id = ca.user_id) " +
                "WHERE u.email = ?1",
        nativeQuery = true,
    )
    fun searchByAuthorMail(
        @Param("email") email: String,
    ): List<Course>
}
