package com.demo.service

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toCourseModel
import com.demo.model.CourseModel
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
public class CourseService(private val courseRepository: CourseRepository, private val userRepository: UserRepository) {
    fun save(course: CourseDTO): CourseModel? {
        val user: User? = userRepository.findByUsername(course.author)
        return user?.let {
            val courseEntity = Course(null, course.title, course.description, course.author, course.completed)
            val savedCourseEntity = courseRepository.save(courseEntity)
            savedCourseEntity.toCourseModel()
        }
    }

    fun update(
        id: Long,
        courseDTO: CourseDTO,
    ): CourseModel {
        var course =
            courseRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Course not found") }

        course.title = courseDTO.title
        course.description = courseDTO.description
        course.completed = courseDTO.completed

        val user: User? = userRepository.findByUsername(courseDTO.author)
        course.author = user?.username ?: course.author

        return courseRepository.save(course).toCourseModel()
    }

    fun fetchAll(): List<CourseModel> {
        val listCourseEntities = courseRepository.findAll()
        return listCourseEntities.map { course -> course.toCourseModel() }
    }

    fun fetchOne(id: Long): Optional<CourseModel> {
        val course: Optional<Course> = courseRepository.findById(id)
        return course.map { it.toCourseModel() }
    }

    fun foundOne(id: Long): Boolean {
        return courseRepository.existsById(id)
    }

    fun delete(id: Long) {
        courseRepository.deleteById(id)
    }

    fun searchByAuthorMail(email: String): List<CourseModel>? {
        val listCourseEntities = courseRepository.searchByAuthorMail(email)
        return listCourseEntities.map { course -> course.toCourseModel() }
    }
}
