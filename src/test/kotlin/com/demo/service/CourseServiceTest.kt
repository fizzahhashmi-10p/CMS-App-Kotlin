package com.demo.service

import com.demo.entity.Course
import com.demo.entity.toCourseModel
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.util.Optional

@SpringBootTest
class CourseServiceTest {
    val id: Long = 10
    val title = "Test Course"
    val description = "Test course Description"
    val author = "Test Teacher"
    val completed = true

    companion object {
        private lateinit var courseRepository: CourseRepository
        private lateinit var userRepository: UserRepository
        private lateinit var courseService: CourseService

        @BeforeAll
        @JvmStatic
        fun setup() {
            courseRepository = mock(CourseRepository::class.java)
            userRepository = mock(UserRepository::class.java)
            courseService = CourseService(courseRepository, userRepository)
        }
    }

    @Test
    fun testSave() {
        val courseModel =
            CourseModel(
                title = title,
                description = description,
                author = author,
                completed = completed,
            )

        // Mock
        val course = courseModel.toCourse()
        val savedCourse = course.copy(id = 10)
        `when`(courseRepository.save(course)).thenReturn(savedCourse)

        val courseDTO = courseModel.toCourseDTO()
        val result = courseService.save(courseDTO)

        println(result)
    }

    @Test
    fun testUpdate() {
        val courseModel =
            CourseModel(
                id = id,
                title = "Updated Test Title",
                description = description,
                author = author,
                completed = completed,
            )
        // Mock
        val course = courseModel.toCourse()
        `when`(courseRepository.save(course)).thenReturn(course)

        val result = courseService.update(courseModel)
        assertEquals(result, courseModel)
    }

    @Test
    fun testFetchAll() {
        val course =
            Course(
                id = id,
                title = title,
                description = description,
                author = author,
                completed = completed,
            )
        // mock
        val courseList = mutableListOf(course)
        `when`(courseRepository.findAll()).thenReturn(courseList)

        val courseModel = course.toCourseModel()
        val courseModelList = mutableListOf(courseModel)

        assertEquals(courseService.fetchAll(), courseModelList)
    }

    @Test
    fun testFetchOne() {
        val course =
            Course(
                id = id,
                title = title,
                description = description,
                author = author,
                completed = completed,
            )
        // mock
        `when`(courseRepository.findById(10)).thenReturn(Optional.of(course))

        val courseModel = course.toCourseModel()
        assertEquals(courseService.fetchOne(10), Optional.of(courseModel))
    }

    @Test
    fun testFoundOne() {
        `when`(courseRepository.existsById(10)).thenReturn(true)
        assertTrue(courseService.foundOne(10))
    }

    @Test
    fun testDelete() {
        assertDoesNotThrow { courseService.delete(id = 10) }

        // Verify that the deleteById method of courseRepository is called with the correct userId
        verify(courseRepository, times(1)).deleteById(10)
    }
}
