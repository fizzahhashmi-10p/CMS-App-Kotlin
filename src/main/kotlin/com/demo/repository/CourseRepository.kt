package com.demo.repository

import com.demo.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface CourseRepository : JpaRepository<Course, Long> {
    fun findByAuthor(author: String): List<Course>

    @Query(value = "SELECT * FROM public.course WHERE author = (SELECT username FROM public.user WHERE email = :email)", nativeQuery = true)
    fun searchByAuthorMail(
        @Param("email") email: String,
    ): List<Course>
}
