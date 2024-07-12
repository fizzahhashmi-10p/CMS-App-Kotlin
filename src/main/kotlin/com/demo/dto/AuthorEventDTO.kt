package com.demo.dto

data class AuthorEventDTO(
    val message: String,
    val author: UserDTO
) : KafkaMessageDTO