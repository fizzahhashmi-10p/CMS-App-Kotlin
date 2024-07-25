package com.demo.controller

import com.demo.dto.CourseDTO
import com.demo.dto.CourseEventDTO
import com.demo.dto.serializeCourseEvent
import com.demo.service.CourseService
import io.awspring.cloud.sqs.operations.SqsTemplate
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
@RequestMapping("/courses")
public class CourseController(private val courseService: CourseService, private val sqsTemplate: SqsTemplate) {
    @PostMapping
    fun createCourse(
        @RequestBody course: CourseDTO,
    ): ResponseEntity<Any> {
        return ResponseEntity("Request to add course is sent successfully", HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllCourses(): List<CourseDTO> {
        return courseService.fetchAll()
    }

    @GetMapping("/{id}")
    fun getCourseById(
        @PathVariable id: Long,
    ): ResponseEntity<CourseDTO> {
        return ResponseEntity.ok(courseService.fetchOne(id))
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: Long,
        @RequestBody courseDTO: CourseDTO,
    ): ResponseEntity<Any> {
        sqsTemplate.send("course-queue",
            serializeCourseEvent(CourseEventDTO("update-course", courseDTO, id ))
        )
        return ResponseEntity.ok().body("Request to update course is sent successfully")
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(
        @PathVariable id: Long,
    ) {
        courseService.delete(id)
    }

    @GetMapping("/search")
    fun searchCourses(
        @RequestParam email: String,
    ): ResponseEntity<List<CourseDTO>> {
        val courses = courseService.searchByAuthorMail(email)
        return ResponseEntity.ok(courses)
    }
}
