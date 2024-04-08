package com.demo.service

import com.demo.model.Course
import com.demo.repository.CourseRespository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CourseService(private val courseRepository: CourseRespository) {

    fun saveAndUpdate(course: Course): Course{
        return courseRepository.save(course)
    }

    fun fetchAll(): List<Course>{
        return courseRepository.findAll()
    }

    fun fetchOne(id: Long): Optional<Course> {
        return courseRepository.findById(id)
    }

    fun foundOne(id: Long): Boolean{
        return courseRepository.existsById(id)
    }

    fun delete(id: Long){
        courseRepository.deleteById(id)
    }

}