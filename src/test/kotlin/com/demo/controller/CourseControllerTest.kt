package com.demo.controller

import com.demo.generateTestToken
import com.demo.model.CourseModel
import com.demo.model.toCourseDTO
import com.google.gson.Gson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup(
    Sql(scripts = ["/test-data.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
)
class CourseControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    val token = generateTestToken("testuser@gmail.com")

    var courseModel = CourseModel(id = 10, title = "Test Course", description = "Test course Description", true, mutableListOf())
    var courseDTO = courseModel.toCourseDTO()

    val request = Gson().toJson(courseDTO)

    @Test
    fun testGetCourseById() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/courses/10")
                .header("Authorization", "Bearer $token"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(10))
    }

    @Test
    fun testGetCourseByIdWhenCourseDoesnotExist() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/courses/2")
                .header("Authorization", "Bearer $token"),
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun testGetCourseByIdWithoutAuthorization() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/courses/10"),
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testCreateCourse() {
        mockMvc.perform(
            post("/courses")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun testCreateCourseWithoutUserAuthorization() {
        mockMvc.perform(
            post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testUpdateCourse() {
        courseDTO.apply {
            authors = listOf("testuser")
        }

        mockMvc.perform(
            put("/courses/10")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Gson().toJson(courseDTO)),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun testUpdateCourseWithoutUserAuthorization() {
        courseDTO.apply {
            title = "Updated title"
            authors = listOf("testuser")
        }

        mockMvc.perform(
            put("/courses/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Gson().toJson(courseDTO)),
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testDeleteWithoutUserAuthorization() {
        mockMvc.perform(
            delete("/courses/10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testDeleteold() {
        mockMvc.perform(
            delete("/courses/10")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
