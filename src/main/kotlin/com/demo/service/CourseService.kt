package com.demo.service

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toCourseModel
import com.demo.model.CourseModel
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
public class CourseService(private val courseRepository: CourseRepository, private val userRepository: UserRepository) {
    fun save(course: CourseDTO): CourseModel? {
        val user: User? = userRepository.findByUsername(course.author)
        if (user != null) {
            val courseEntity = Course(null, course.title, course.description, course.author, course.completed)
            println(courseEntity)
            val savedCourseEntity = courseRepository.save(courseEntity)
            return savedCourseEntity.toCourseModel()
        } else {
            return null
        }
    }

    fun update(course: CourseModel): CourseModel {
        val courseEntity = Course(id = course.id, course.title, course.description, course.author, course.completed)
        val savedCourseEntity = courseRepository.save(courseEntity)
        return savedCourseEntity.toCourseModel()
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
