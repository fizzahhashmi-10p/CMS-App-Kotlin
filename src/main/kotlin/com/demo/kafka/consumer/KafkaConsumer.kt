package com.demo.kafka.consumer

import com.demo.util.Constants.COURSE_GROUP_ID
import com.demo.util.Constants.COURSE_ADDED_TOPIC
import com.demo.util.Constants.COURSE_DELETED_TOPIC
import com.demo.util.Constants.COURSE_UPDATED_TOPIC
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    @KafkaListener(topics = [COURSE_ADDED_TOPIC, COURSE_DELETED_TOPIC, COURSE_UPDATED_TOPIC], groupId = COURSE_GROUP_ID)
    fun CourseListener(message: String) {
        logger.info("Message received: [$message]")
    }

}