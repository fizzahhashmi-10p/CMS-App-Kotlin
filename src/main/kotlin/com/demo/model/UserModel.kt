package com.demo.model

import com.demo.dto.UserDTO
import com.demo.entity.User
import com.demo.util.Role

public data class UserModel(
    val id: Long? = null,
    val username: String,
    val email: String,
    val role: Role,
    val password: String?,
)

fun getUsernames(users: MutableList<User>): List<String> {
    val usernames = mutableListOf<String>()
    for (user in users) {
        usernames.add(user.username)
    }
    return usernames
}

fun UserModel.toUserDTO(): UserDTO {
    return UserDTO(username, email, role.name, null)
}
