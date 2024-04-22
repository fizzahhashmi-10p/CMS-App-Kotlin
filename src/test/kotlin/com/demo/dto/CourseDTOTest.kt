package com.demo.dto

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CourseDTOTest {
    @Test
    fun testCourseDTO(){
        val courseDTO = CourseDTO(
            title = "Test DTO Object",
            description = "Test DTO object description",
            author = "Test Author",
            completed = true
        )

        assertTrue(courseDTO is CourseDTO)
    }
}