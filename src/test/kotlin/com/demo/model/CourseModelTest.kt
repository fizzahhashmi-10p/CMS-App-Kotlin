package com.demo.model

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CourseModelTest {

    @Test
    fun testCourseModel(){
        val courseModel = CourseModel(
            id = 10,
            title = "Test Model Object",
            description = "Test model object description",
            author = "Test Author",
            completed = false
        )

        // Test Case: Check if model object is created successfully
        assertTrue(courseModel is CourseModel)

        // Test Case: Check model to Entity conversion
        val courseEntity = courseModel.toCourse()
        assertTrue(courseEntity is Course)

        // Test Case: Check model to DTO conversion
        val courseDTO = courseModel.toCourseDTO()
        assertTrue(courseDTO is CourseDTO)
    }
}