package com.demo.kafka.producer

import org.springframework.kafka.core.KafkaTemplate
import com.demo.util.Constants
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun sendStringMessage(message: String) {
        kafkaTemplate.send(Constants.TOPIC_NAME, message)
    }

}