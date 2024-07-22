package com.demo.service

import com.demo.dto.CourseDTO
import com.demo.dto.CourseEventDTO
import com.demo.dto.StringEventDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toCourseModel
import com.demo.exception.ResourceNotFoundException
import com.demo.exception.ValidationException
import com.demo.model.toCourseDTO
import com.demo.repository.CourseRepository
import com.demo.repository.UserRepository
import com.demo.kafka.producer.KafkaProducer
import com.demo.util.Constants
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse


@Service
public class CourseService(private val courseRepository: CourseRepository, private val userRepository: UserRepository, private val kafkaProducer: KafkaProducer) {
    fun save(course: CourseDTO): CourseDTO? {
        val authors: MutableList<User> = userRepository.findAllByUsernameIn(course.authors)
        return when {
            (authors.size == course.authors.size) -> {
                val newCourse = courseRepository.save(
                    Course(null, course.title, course.description, course.completed, authors),
                ).toCourseModel().toCourseDTO()
                kafkaProducer.sendMessage(Constants.COURSE_ADDED_TOPIC,CourseEventDTO("New Topic Successfully Added", newCourse ))
                return newCourse
            }
            else -> throw(ValidationException("Invalid Author name provided"))
        }
    }

    fun update(
        id: Long?,
        courseDTO: CourseDTO,
    ): CourseDTO {
        if (id == null) {
            throw ValidationException("Error: ID cannot be null")
        }
        var course =
            courseRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Course not found") }

        var authorList = userRepository.findAllByUsernameIn(courseDTO.authors)
        val updatedCourse = courseRepository.save(
            course.apply {
                title = courseDTO.title
                description = courseDTO.description
                completed = courseDTO.completed
                authors =
                    when {
                        (authorList.size == course.authors.size) -> authorList
                        else -> throw (ValidationException("Invalid author provided."))
                    }
            },
        ).toCourseModel().toCourseDTO()
        kafkaProducer.sendMessage(Constants.COURSE_UPDATED_TOPIC,CourseEventDTO("Course: ${id} is updated successfully", updatedCourse))
        return updatedCourse
    }

    fun fetchAll(): List<CourseDTO> {
        val listCourseEntities = courseRepository.findAll()
        return listCourseEntities.map { course -> (course.toCourseModel()).toCourseDTO() }
    }

    fun fetchOne(id: Long): CourseDTO {
        return courseRepository.findById(id).map {
            (it.toCourseModel()).toCourseDTO()
        }.getOrElse { throw ResourceNotFoundException("Course with id: $id does not exist.") }
    }

    fun foundOne(id: Long): Boolean {
        return courseRepository.existsById(id)
    }

    fun delete(id: Long) {
        if (foundOne(id)) {
            courseRepository.deleteById(id)
            kafkaProducer.sendMessage(Constants.COURSE_DELETED_TOPIC,StringEventDTO("Course id: ${id} is deleted."))
        } else {
            throw ValidationException("Course with id: $id not found.")
        }
    }

    fun searchByAuthorMail(email: String): List<CourseDTO>? {
        val listCourseEntities = courseRepository.searchByAuthorMail(email)
        return listCourseEntities.map { course -> (course.toCourseModel()).toCourseDTO() }
    }
}
