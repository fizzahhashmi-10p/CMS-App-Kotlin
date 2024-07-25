package com.demo.service

import com.demo.dto.KafkaMessageDTO
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, KafkaMessageDTO>
) {

    fun sendMessage(topic: String, message: KafkaMessageDTO) {
        kafkaTemplate.send(topic, message)
    }

}