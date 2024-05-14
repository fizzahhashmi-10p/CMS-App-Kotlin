package com.demo.service

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toCourseModel
import com.demo.exception.ResourceNotFoundException
import com.demo.exception.ValidationException
import com.demo.model.CourseModel
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
public class CourseService(private val courseRepository: CourseRepository, private val userRepository: UserRepository) {
    fun save(course: CourseDTO): CourseModel? {
        val user: User? = userRepository.findByUsername(course.author)
        return user?.let {
            val courseEntity = Course(null, course.title, course.description, course.author, course.completed)
            val savedCourseEntity = courseRepository.save(courseEntity)
            savedCourseEntity.toCourseModel()
        } ?: throw(ValidationException("Invalid Author name provided"))
    }

    fun update(
        id: Long,
        courseDTO: CourseDTO,
    ): CourseModel {
        var course =
            courseRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Course not found") }

        course.title = courseDTO.title
        course.description = courseDTO.description
        course.completed = courseDTO.completed

        val user = userRepository.findByUsername(courseDTO.author) ?: throw (ValidationException("Invalid author provided."))
        course.author = user.username

        return courseRepository.save(course).toCourseModel()
    }

    fun fetchAll(): List<CourseModel> {
        val listCourseEntities = courseRepository.findAll()
        return listCourseEntities.map { course -> course.toCourseModel() }
    }

    fun fetchOne(id: Long): CourseModel {
        return courseRepository.findById(id).map {
            it.toCourseModel()
        }.getOrElse { throw ResourceNotFoundException("Course with id: $id does not exist.") }
    }

    fun foundOne(id: Long): Boolean {
        return courseRepository.existsById(id)
    }

    fun delete(id: Long) {
        if (foundOne(id)) {
            courseRepository.deleteById(id)
        } else {
            throw ValidationException("Course with id: $id not found.")
        }
    }

    fun searchByAuthorMail(email: String): List<CourseModel>? {
        val listCourseEntities = courseRepository.searchByAuthorMail(email)
        return listCourseEntities.map { course -> course.toCourseModel() }
    }
}
