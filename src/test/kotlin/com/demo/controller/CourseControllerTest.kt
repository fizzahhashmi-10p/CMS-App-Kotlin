package com.demo.controller

import com.demo.entity.Course
import com.demo.service.CourseService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(CourseController::class)
class CourseControllerTest {

    val request = "{\"title\": \"Test Course\",\"description\": \"Test course Description\",\"author\": \"Test Teacher\"}"
    val response = "{\"id\":10, \"title\": \"Test Course\",\"description\": \"Test course Description\",\"author\": \"Test Teacher\"}"


    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var courseService: CourseService

    @Test
    fun testCreateCourse() {
        val course = Course(title = "Test Course", description =  "Test course Description", author = "Test Teacher")
        val courseCreated = course.copy(id = 10)

        `when`(courseService.saveAndUpdate(course)).thenReturn(courseCreated)
        mockMvc.perform(post("/course")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated)
    }

    @Test
    fun testGetCourseById() {
        val course = Course(id = 10, title = "Test Course", description =  "Test course Description", author = "Test Teacher")

        `when`(courseService.fetchOne(10)).thenReturn(Optional.of(course))
        mockMvc.perform(get("/course/10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().json(response))
    }

    @Test
    fun testUpdateCourse() {
        val course = Course(id = 10, title = "Test Course", description =  "Test course Description", author = "Test Teacher")
        val courseUpdated = course.copy(title = "Updated Title")

        `when`(courseService.saveAndUpdate(course)).thenReturn(courseUpdated)
        `when`(courseService.fetchOne(10)).thenReturn(Optional.of(course))

        mockMvc.perform(put("/course/10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
    }

    @Test
    fun `testDelete`() {
        `when`(courseService.foundOne(10)).thenReturn(true)
        Mockito.doNothing().`when`(courseService).delete(10)

        mockMvc.perform(delete("/course/10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }


}
