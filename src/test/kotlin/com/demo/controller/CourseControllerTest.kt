package com.demo.controller

import com.demo.entity.User
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.service.CourseService
import com.google.gson.Gson
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CourseController::class)
class CourseControllerTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    val id: Long = 10
    private final val author: User = entityManager.find(User::class.java, 1)
    private var courseModel =
        CourseModel(id = id, title = "Test Course", description = "Test course Description", true, mutableListOf(author))

    val courseDTO = courseModel.toCourseDTO()
    val course = courseModel.toCourse()

    val gson = Gson()
    val request = gson.toJson(courseDTO)
    val response = gson.toJson(courseModel)

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var courseService: CourseService

    @Test
    fun testCreateCourse() {
        `when`(courseService.save(courseDTO)).thenReturn(courseDTO)
        mockMvc.perform(
            post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testGetCourseById() {
        `when`(courseService.fetchOne(id)).thenReturn(courseDTO)
        mockMvc.perform(
            get("/courses/10")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(content().json(response))
    }

    @Test
    fun testUpdateCourse() {
        val id: Long = 10
        val requestBody = courseDTO.copy(title = "Updated Title")
        val courseUpdated = courseDTO.copy(title = "Updated Title")

        `when`(courseService.update(id, requestBody)).thenReturn(courseUpdated)

        mockMvc.perform(
            put("/courses/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun testDelete() {
        `when`(courseService.foundOne(id)).thenReturn(true)
        Mockito.doNothing().`when`(courseService).delete(id)

        mockMvc.perform(
            delete("/courses/$id")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
    }
}
