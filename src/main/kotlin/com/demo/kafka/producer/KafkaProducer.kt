package com.demo.kafka.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun sendStringMessage(topic: String, message: String) {
        kafkaTemplate.send(topic, message)
    }

}