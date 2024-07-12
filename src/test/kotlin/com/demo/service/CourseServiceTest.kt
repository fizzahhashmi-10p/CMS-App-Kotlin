package com.demo.service

import com.demo.entity.User
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import com.demo.util.Role
import com.demo.kafka.producer.KafkaProducer
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.util.Optional

@SpringBootTest
class CourseServiceTest() {
    final val id: Long = 10
    val author: User = User(10, "testuser", "testuser@gmail.com", Role.USER, mutableListOf(), "password")

    val courseRepository: CourseRepository = mock(CourseRepository::class.java)
    val userRepository: UserRepository = mock(UserRepository::class.java)
    val kafkaProducer: KafkaProducer = mock(KafkaProducer::class.java)
    val courseService: CourseService = CourseService(courseRepository, userRepository, kafkaProducer )

    private final val courseModel =
        CourseModel(
            10,
            "Test Course",
            "Test course Description",
            true,
            mutableListOf(author),
        )

    val course = courseModel.toCourse()
    val courseDTO = courseModel.toCourseDTO()

    @Test
    fun testSave() {
        // Mock

        `when`(courseRepository.save(course.copy(id = null))).thenReturn(course)
        `when`(userRepository.findAllByUsernameIn(listOf(author.username))).thenReturn(mutableListOf(author))

        assertEquals(courseService.save(courseDTO)?.id, 10)
    }

    @Test
    fun testUpdate() {
        val updatedTitle = "Updated test title"
        `when`(courseRepository.findById(id)).thenReturn(Optional.of(course))
        `when`(courseRepository.save(course.copy(title = updatedTitle))).thenReturn(course.copy(title = updatedTitle))
        `when`(userRepository.findAllByUsernameIn(listOf(author.username))).thenReturn(mutableListOf(author))

        assertEquals(courseService.update(id, courseDTO.copy(title = updatedTitle)).title, updatedTitle)
    }

    @Test
    fun testFetchAll() {
        // mock
        `when`(courseRepository.findAll()).thenReturn(mutableListOf(course))

        assert(courseService.fetchAll().size > 0)
    }

    @Test
    fun testFetchOne() {
        // mock
        `when`(courseRepository.findById(id)).thenReturn(Optional.of(course))

        assertEquals(courseService.fetchOne(id).id, 10)
    }

    @Test
    fun testFoundOne() {
        `when`(courseRepository.existsById(id)).thenReturn(true)
        assertTrue(courseService.foundOne(id))
    }

    @Test
    fun testDelete() {
        `when`(courseService.foundOne(id)).thenReturn(true)
        assertDoesNotThrow { courseService.delete(id) }

        // Verify that the deleteById method of courseRepository is called with the correct userId
        verify(courseRepository, times(1)).deleteById(id)
    }
}
