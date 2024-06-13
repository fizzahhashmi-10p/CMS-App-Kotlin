package com.demo.repository

import com.demo.entity.Course
import com.demo.entity.User
import com.demo.util.Role
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourseRepositoryTest {
    companion object {
        val db = PostgreSQLContainer("postgres")

        @BeforeAll
        @JvmStatic
        fun startDB() = db.start()

        @AfterAll
        @JvmStatic
        fun stopDB() = db.stop()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }

    var author = User(null, "testuser", "testuser@gmail.com", Role.USER, mutableListOf(), "password")
    val course = Course(null, "Math", "Mathematics course", true, mutableListOf())

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun testSaveCourse() = assertNotNull(courseRepository.save(course).id)

    @Test
    fun testFindCourseByAuthor() {
        course.authors = mutableListOf(userRepository.save(author))
        courseRepository.save(course)
        assert(courseRepository.searchByAuthorMail(author.email).all { course -> course.authors.contains(author) })
    }
}
