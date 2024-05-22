package com.demo.dto

import com.demo.util.Role

data class UserDTO(
    val username: String,
    val email: String,
    val role: Role,
    val password: String,
)
