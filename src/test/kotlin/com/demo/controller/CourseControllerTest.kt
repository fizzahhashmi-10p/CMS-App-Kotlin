package com.demo.controller

import com.demo.model.Course
import com.demo.service.CourseService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(CourseController::class)
class CourseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var courseService: CourseService

    @Test
    fun `Test getCourseById`() {
        // Given
        val courseId = 1L
        val course = Course(courseId, "Math", "Mathematics course")

        // Mock the service method
        `when`(courseService.getCourseById(anyLong())).thenReturn(course)

        // When/Then
        mockMvc.perform(get("/courses/$courseId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(courseId.toInt()))
            .andExpect(jsonPath("$.name").value("Math"))
            .andExpect(jsonPath("$.description").value("Mathematics course"))
    }
}
