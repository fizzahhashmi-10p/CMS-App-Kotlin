package com.demo.repository

import com.demo.entity.Course
import com.demo.entity.User
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourseRepositoryTest {
    var author = entityManager.find(User::class.java, 1)

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Test
    fun testSaveCourse() {
        val course =
            Course(
                null,
                "Math",
                "Mathematics course",
                true,
                mutableListOf(author),
            )

        val savedCourse = courseRepository.save(course)

        assertNotNull(savedCourse.id)
        assertNotNull(entityManager.find(Course::class.java, savedCourse.id))
    }

    @Test
    fun testFindCourseByAuthor() {
        assert(courseRepository.searchByAuthorMail(author.email).all { course -> course.authors.contains(author) })
    }
}
