package com.demo.repository

import com.demo.entity.Course
import com.demo.entity.User
import com.demo.util.Role
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourseRepositoryTest {
    var author = User(10, "testuser", "testuser@gmail.com", Role.USER, mutableListOf(), "password")

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Test
    fun testSaveCourse() {
        val savedCourse =
            courseRepository.save(
                Course(
                    null,
                    "Math",
                    "Mathematics course",
                    true,
                    mutableListOf(author),
                ),
            )

        assertNotNull(savedCourse.id)
    }

    @Test
    fun testFindCourseByAuthor() {
        assert(courseRepository.searchByAuthorMail(author.email).all { course -> course.authors.contains(author) })
    }
}
