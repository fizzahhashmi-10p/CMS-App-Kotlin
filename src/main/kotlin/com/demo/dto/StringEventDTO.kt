package com.demo.dto

data class StringEventDTO(
    val message: String
) : KafkaMessageDTO
