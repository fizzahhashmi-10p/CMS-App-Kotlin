package com.demo.controller

import com.demo.generateTestToken
import com.demo.model.CourseModel
import com.demo.model.toCourseDTO
import com.google.gson.Gson
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Transactional
    @BeforeEach
    fun insertTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    @AfterEach
    fun cleanupTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data-cleanup.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }

    val token = generateTestToken("testuser@gmail.com")

    var courseModel = CourseModel(id = 30, title = "Test Course", description = "Test course Description", true, mutableListOf())
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
