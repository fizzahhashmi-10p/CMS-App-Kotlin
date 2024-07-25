package com.demo.controller

import com.demo.dto.StringEventDTO
import com.demo.service.KafkaProducer
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class KafkaController (private val kafkaProducer: KafkaProducer) {
    @PostMapping("/test")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendTestMessage(
        @RequestBody requestBody: RequestBodyDto
    ) {
        kafkaProducer.sendMessage(
            topic = requestBody.topic,
            StringEventDTO(requestBody.message)
        )
    }

    data class RequestBodyDto(val topic: String, val message: String)
}
