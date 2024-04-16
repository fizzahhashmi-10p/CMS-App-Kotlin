package com.demo.service

import com.demo.dto.CourseDTO
import com.demo.entity.Course
import com.demo.entity.toCourseModel
import com.demo.model.CourseModel
import com.demo.repository.CourseRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
public class CourseService(private val courseRepository: CourseRepository) {

    fun save(course: CourseDTO): CourseModel {
        val courseEntity = Course(null, course.title, course.description, course.author, course.completed)
        val savedCourseEntity = courseRepository.save(courseEntity)
        return savedCourseEntity.toCourseModel()
    }

    fun update(course: CourseModel): CourseModel {
        val courseEntity = Course(id = course.id, course.title, course.description, course.author, course.completed)
        val savedCourseEntity = courseRepository.save(courseEntity)
        return savedCourseEntity.toCourseModel()
    }

    fun fetchAll(): List<CourseModel>{
        val listCourseEntities = courseRepository.findAll()
        return listCourseEntities.map { course ->  course.toCourseModel()}
    }

    fun fetchOne(id: Long): Optional<CourseModel> {
        val course: Optional<Course> = courseRepository.findById(id)
        return course.map { it.toCourseModel() }
    }

    fun foundOne(id: Long): Boolean{
        return courseRepository.existsById(id)
    }

    fun delete(id: Long){
        courseRepository.deleteById(id)
    }

}