package com.demo.config

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.kafka.test.EmbeddedKafkaBroker

import com.demo.util.Constants.COURSE_ADDED_TOPIC
import com.demo.util.Constants.COURSE_UPDATED_TOPIC
import com.demo.util.Constants.COURSE_DELETED_TOPIC


fun CourseConsumerTestConfig(broker: EmbeddedKafkaBroker): KafkaConsumer<String, String> {
    val consumerProps = KafkaTestUtils.consumerProps("Test-course-group", "false", broker)
    val consumer = KafkaConsumer<String, String>(consumerProps, StringDeserializer(), StringDeserializer())
    consumer.subscribe(listOf(COURSE_ADDED_TOPIC, COURSE_UPDATED_TOPIC, COURSE_DELETED_TOPIC))
    return consumer
}
