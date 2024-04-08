package com.demo.controller

import com.demo.model.Course
import com.demo.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/course")
class CourseController(private val courseService: CourseService) {

    @PostMapping
    fun createCourse(@RequestBody course: Course): ResponseEntity<Course> {
        val savedCourse = courseService.saveAndUpdate(course)
        return ResponseEntity(savedCourse, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllCourses(): List<Course> {
        return courseService.fetchAll()
    }

    @GetMapping("/{id}")
    fun getCourseById(@PathVariable id: Long): ResponseEntity<Course> {
        val course = courseService.fetchOne(id)
        return course.map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun updateCourse(@PathVariable id: Long, @RequestBody data: Course): ResponseEntity<Course> {
        val course = courseService.fetchOne(id)
        return course.map {
            val updatedCourse  = it.copy(
                title = data.title,
                description = data.description,
                author = data.author,
                completed = data.completed
            )
            ResponseEntity.ok().body(courseService.saveAndUpdate(updatedCourse))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(@PathVariable id: Long): ResponseEntity<Void> {
        return if (courseService.foundOne(id)) {
            courseService.delete(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
