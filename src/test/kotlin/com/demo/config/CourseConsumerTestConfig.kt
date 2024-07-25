package com.demo.config

import com.demo.dto.KafkaMessageDTO
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.kafka.test.EmbeddedKafkaBroker

import com.demo.util.Constants.COURSE_ADDED_TOPIC
import com.demo.util.Constants.COURSE_UPDATED_TOPIC
import com.demo.util.Constants.COURSE_DELETED_TOPIC
import org.apache.kafka.clients.consumer.ConsumerConfig


fun CourseConsumerTestConfig(broker: EmbeddedKafkaBroker): KafkaConsumer<String, KafkaMessageDTO> {
    val consumerProps = KafkaTestUtils.consumerProps("Test-course-group", "false", broker)
    consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "com.demo.dto"
    val consumer = KafkaConsumer<String, KafkaMessageDTO>(consumerProps, StringDeserializer(), JsonDeserializer(
        KafkaMessageDTO::class.java))
    consumer.subscribe(listOf(COURSE_ADDED_TOPIC, COURSE_UPDATED_TOPIC, COURSE_DELETED_TOPIC))
    return consumer
}
