package com.demo.model

import com.demo.entity.User
import com.demo.util.Role

public data class UserModel(
    val id: Long? = null,
    val username: String,
    val email: String,
    val role: Role,
)

fun getUsernames(users: MutableList<User>): List<String> {
    val usernames = mutableListOf<String>()
    for (user in users) {
        usernames.add(user.username)
    }
    return usernames
}
