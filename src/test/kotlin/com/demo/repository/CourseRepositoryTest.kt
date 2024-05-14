package com.demo.repository

import com.demo.entity.Course
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CourseRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Test
    fun testSaveCourse() {
        val course = Course(title = "Math", description = "Mathematics course", author = "testuser")

        val savedCourse = courseRepository.save(course)

        assert(savedCourse.id != null)
        val retrievedCourse = entityManager.find(Course::class.java, savedCourse.id)
        assert(retrievedCourse != null)
        assert(retrievedCourse.title == course.title)
        assert(retrievedCourse.description == course.description)
    }

    @Test
    fun testFindCourseByAuthor() {
        val authorName = "Math Teacher"
        val foundCourses = courseRepository.findByAuthor(authorName)

        assert(foundCourses != null)
        assertThat(foundCourses).allMatch { it.author == authorName }
    }
}
