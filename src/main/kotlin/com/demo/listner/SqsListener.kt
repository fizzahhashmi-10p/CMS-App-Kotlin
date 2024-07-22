package com.demo.listner

import com.demo.dto.CourseDTO
import com.demo.dto.CourseEventDTO
import com.demo.dto.deserializeCourseEvent
import com.demo.eventHandler.CourseEventHandler
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.web.bind.annotation.RestController

@RestController
class SqsListener(private val courseEventHandler: CourseEventHandler) {

    @SqsListener("course-queue")
    fun listen(body: String): CourseDTO? {
            val courseEventDTO: CourseEventDTO = deserializeCourseEvent(body)
            return when(courseEventDTO.message) {
                "create-course" -> courseEventHandler.addCourseHandler(courseEventDTO.course)
                "update-course" -> courseEventHandler.updateCourseHandler(courseEventDTO.id, courseEventDTO.course)
                else -> throw (Exception("Invalid event Provided"))
            }
    }

}