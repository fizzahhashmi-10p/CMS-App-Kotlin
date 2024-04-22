package com.demo.entity

import com.demo.model.CourseModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CourseTest {
    @Test
    fun testCourse(){
        val course = Course(
            title = "Test Course Object",
            description = "Test course object description",
            author = "Test Author",
            completed = true
        )

        // Test Case: Course Successful Creation
        assertTrue(course is Course)
        assertNotNull(course.id)

        // Test Case: Course Entity to Model conversion
        val courseModel = course.toCourseModel()
        assertTrue(courseModel is CourseModel)
    }

}
