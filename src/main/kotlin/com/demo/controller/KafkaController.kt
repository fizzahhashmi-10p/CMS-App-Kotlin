package com.demo.controller

import com.demo.kafka.producer.KafkaProducer
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
        kafkaProducer.sendStringMessage(
            message = requestBody.message
        )
    }

    data class RequestBodyDto(val message: String)
}
