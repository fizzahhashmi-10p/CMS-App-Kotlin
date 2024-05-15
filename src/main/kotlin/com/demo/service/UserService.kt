package com.demo.service

import com.demo.dto.UserDTO
import com.demo.entity.User
import com.demo.entity.toUserModel
import com.demo.model.UserModel
import com.demo.repository.UserRepository
import com.demo.util.Role
import org.springframework.stereotype.Service

@Service
public class UserService(private val userRepository: UserRepository) {
    fun save(user: UserDTO): UserModel? {
        val role =
            when (user.role) {
                "admin" -> Role.ADMIN
                "user" -> Role.USER
                else -> Role.USER // Default user
            }

        val userEntity = User(null, user.username, user.email, role)
        return (userRepository.save(userEntity)).toUserModel()
    }

    fun fetchAll(): List<UserModel> {
        val listUserEntities = userRepository.findAll()
        return listUserEntities.map { user -> user.toUserModel() }
    }

    fun findAllByUsernames(usernames: List<String>): List<User> {
        return userRepository.findAllByUsernameIn(usernames)
    }
}
