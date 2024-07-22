package com.demo.eventHandler

import com.demo.dto.CourseDTO
import com.demo.service.CourseService
import org.springframework.stereotype.Component


@Component
class CourseEventHandler (private val courseService: CourseService) {
    fun addCourseHandler(course: CourseDTO) =  courseService.save(course)
    fun updateCourseHandler(id: Long?, course: CourseDTO) =  courseService.update(id, course)
}