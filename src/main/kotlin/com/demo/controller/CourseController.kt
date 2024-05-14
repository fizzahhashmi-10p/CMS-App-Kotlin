package com.demo.controller

import com.demo.dto.CourseDTO
import com.demo.model.CourseModel
import com.demo.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/course")
public class CourseController(private val courseService: CourseService) {
    @PostMapping
    fun createCourse(
        @RequestBody course: CourseDTO,
    ): ResponseEntity<Any> {
        val savedCourse: CourseModel? = courseService.save(course)
        return if (savedCourse != null) {
            ResponseEntity(savedCourse, HttpStatus.CREATED)
        } else {
            ResponseEntity.badRequest().body("Invalid Author Provided")
        }
    }

    @GetMapping
    fun getAllCourses(): List<CourseModel> {
        return courseService.fetchAll()
    }

    @GetMapping("/{id}")
    fun getCourseById(
        @PathVariable id: Long,
    ): ResponseEntity<CourseModel> {
        val course = courseService.fetchOne(id)
        return course.map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: Long,
        @RequestBody courseDTO: CourseDTO,
    ): ResponseEntity<CourseModel> {
        val courseModel = courseService.fetchOne(id)
        return courseModel.map {
            val updatedCourse: CourseModel =
                it.copy(
                    id = id,
                    title = courseDTO.title,
                    description = courseDTO.description,
                    author = courseDTO.author,
                    completed = courseDTO.completed,
                )
            ResponseEntity.ok().body(courseService.update(updatedCourse))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        return if (courseService.foundOne(id)) {
            courseService.delete(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchCourses(
        @RequestParam email: String,
    ): ResponseEntity<List<CourseModel>> {
        val courses = courseService.searchByAuthorMail(email)
        return ResponseEntity.ok(courses)
    }
}
