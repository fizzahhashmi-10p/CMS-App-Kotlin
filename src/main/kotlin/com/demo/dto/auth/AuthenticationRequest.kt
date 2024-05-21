package com.demo.dto.auth

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
