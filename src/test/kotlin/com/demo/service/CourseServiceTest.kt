package com.demo.service

import com.demo.entity.User
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import java.util.Optional

@SpringBootTest
class CourseServiceTest {
    final val id: Long = 10
    private final val author: User = entityManager.find(User::class.java, 1)

    private final val courseRepository: CourseRepository = mock(CourseRepository::class.java)
    private final val userRepository: UserRepository = mock(UserRepository::class.java)
    val courseService: CourseService = CourseService(courseRepository, userRepository)

    @Autowired
    private lateinit var entityManager: TestEntityManager

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

        `when`(courseRepository.save(course)).thenReturn(course)
        `when`(userRepository.findAllByUsernameIn(listOf(author.username))).thenReturn(mutableListOf(author))

        assertEquals(courseService.save(courseDTO), courseModel)
    }

    @Test
    fun testUpdate() {
        val updatedCourseModel =
            courseModel.copy(title = "Updated test title")

        `when`(courseRepository.findById(id)).thenReturn(Optional.of(course))
        `when`(courseRepository.save(updatedCourseModel.toCourse())).thenReturn(updatedCourseModel.toCourse())
        `when`(userRepository.findAllByUsernameIn(listOf(author.username))).thenReturn(mutableListOf(author))

        assertEquals(courseService.update(id, courseModel.toCourseDTO()), updatedCourseModel)
    }

    @Test
    fun testFetchAll() {
        // mock
        `when`(courseRepository.findAll()).thenReturn(mutableListOf(course))

        assertEquals(courseService.fetchAll(), mutableListOf(courseModel))
    }

    @Test
    fun testFetchOne() {
        // mock
        `when`(courseRepository.findById(id)).thenReturn(Optional.of(course))

        assertEquals(courseService.fetchOne(id), courseModel)
    }

    @Test
    fun testFoundOne() {
        `when`(courseRepository.existsById(id)).thenReturn(true)
        assertTrue(courseService.foundOne(id))
    }

    @Test
    fun testDelete() {
        assertDoesNotThrow { courseService.delete(id) }

        // Verify that the deleteById method of courseRepository is called with the correct userId
        verify(courseRepository, times(1)).deleteById(id)
    }
}
