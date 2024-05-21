package com.demo.service

import com.demo.dto.UserDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toUserModel
import com.demo.model.UserModel
import com.demo.model.toUserDTO
import com.demo.repository.UserRepository
import com.demo.util.Role
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
public class UserService(private val userRepository: UserRepository, private val encoder: PasswordEncoder) {
    fun save(user: UserDTO): UserModel? {
        val role =
            when (user.role) {
                "admin" -> Role.ADMIN
                "user" -> Role.USER
                else -> Role.USER // Default user
            }

        val userEntity = User(null, user.username, user.email, role, mutableListOf<Course>(), encoder.encode(user.password))
        return (userRepository.save(userEntity)).toUserModel()
    }

    fun fetchAll(): List<UserDTO> {
        val listUserEntities = userRepository.findAll()
        return listUserEntities.map { user -> user.toUserModel().toUserDTO() }
    }

    fun findAllByUsernames(usernames: List<String>): List<User> {
        return userRepository.findAllByUsernameIn(usernames)
    }
}
