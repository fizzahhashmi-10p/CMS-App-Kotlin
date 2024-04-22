package com.demo.controller

import com.demo.dto.CourseDTO
import com.demo.model.CourseModel
import com.demo.service.CourseService
import org.junit.jupiter.api.BeforeAll
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

import com.google.gson.Gson


@WebMvcTest(CourseController::class)
class CourseControllerTest {

    companion object{
        lateinit var request: String
        lateinit var response: String
        lateinit var courseDTO: CourseDTO
        lateinit var courseModel: CourseModel

        @BeforeAll
        @JvmStatic
        fun setup(){
            val gson = Gson()

            val id:Long = 10
            val title = "Test Course"
            val description = "Test course Description"
            val author = "Test Teacher"
            val completed = true

            courseDTO = CourseDTO(title = title, description =  description, author = author, completed = completed)
            courseModel = CourseModel( id = id, title = title, description =  description, author = author, completed = completed)

            request = gson.toJson(courseDTO)
            response = gson.toJson(courseModel)
        }

    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var courseService: CourseService

    @Test
    fun testCreateCourse() {
        `when`(courseService.save(courseDTO)).thenReturn(courseModel)
        mockMvc.perform(post("/course")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated)
    }

    @Test
    fun testGetCourseById() {
        `when`(courseService.fetchOne(10)).thenReturn(Optional.of(courseModel))
        mockMvc.perform(get("/course/10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().json(response))
    }

    @Test
    fun testUpdateCourse() {
        val courseUpdated = courseModel.copy(title = "Updated Title")

        `when`(courseService.update(courseModel)).thenReturn(courseUpdated)
        `when`(courseService.fetchOne(10)).thenReturn(Optional.of(courseModel))

        mockMvc.perform(put("/course/10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Title"))
    }

    @Test
    fun testDelete() {
        `when`(courseService.foundOne(10)).thenReturn(true)
        Mockito.doNothing().`when`(courseService).delete(10)

        mockMvc.perform(delete("/course/10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }
}
