package com.demo.service

import com.demo.dto.UserDTO
import com.demo.entity.Course
import com.demo.entity.User
import com.demo.entity.toUserModel
import com.demo.model.UserModel
import com.demo.model.toUserDTO
import com.demo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
public class UserService(private val userRepository: UserRepository, private val encoder: PasswordEncoder) {
    fun save(user: UserDTO): UserModel? {
        return (
            userRepository.save(
                User(null, user.username, user.email, user.role, mutableListOf<Course>(), encoder.encode(user.password)),
            )
        ).toUserModel()
    }

    fun fetchAll(): List<UserDTO> {
        return userRepository.findAll().map { user -> user.toUserModel().toUserDTO() }
    }

    fun findAllByUsernames(usernames: List<String>): List<User> {
        return userRepository.findAllByUsernameIn(usernames)
    }
}
