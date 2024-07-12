package com.demo.service

import com.demo.config.CourseConsumerTestConfig
import com.demo.dto.UserDTO
import com.demo.entity.User
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration
import com.demo.kafka.producer.KafkaProducer
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.repository.UserRepository
import org.springframework.kafka.test.EmbeddedKafkaBroker
import com.demo.util.Constants
import com.demo.util.Constants.COURSE_ADDED_TOPIC
import com.demo.util.Constants.COURSE_DELETED_TOPIC
import com.demo.util.Constants.COURSE_UPDATED_TOPIC
import com.demo.util.Constants.USER_ADDED_TOPIC
import com.demo.util.Role
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EmbeddedKafka(partitions = 1, topics = [COURSE_ADDED_TOPIC, COURSE_UPDATED_TOPIC, COURSE_DELETED_TOPIC, USER_ADDED_TOPIC], brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class KafkaServiceTest {

    @Autowired
    private lateinit var kafkaProducer: KafkaProducer

    lateinit var records: Iterable<ConsumerRecord<String, String>>

    val author: User = User(10, "testuser", "testuser@gmail.com", Role.USER, mutableListOf(), "password")

    companion object {
        lateinit var consumer: KafkaConsumer<String, String>
        private lateinit var embeddedKafka: EmbeddedKafkaBroker

        @Autowired
        private lateinit var userRepository: UserRepository

        val db = PostgreSQLContainer("postgres")

        @BeforeAll
        @JvmStatic
        fun KafkaSetup(@Autowired embeddedKafka: EmbeddedKafkaBroker) {
            this.embeddedKafka = embeddedKafka
            consumer = CourseConsumerTestConfig(this.embeddedKafka)
        }

        @BeforeAll
        @JvmStatic
        fun testSetup(){
            db.start()
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            embeddedKafka.destroy()
            consumer.close()
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }

    private final val courseModel =
        CourseModel(
            null,
            "Test Course",
            "Test course Description",
            true,
            mutableListOf(author),
        )

    val course = courseModel.toCourse()
    val courseDTO = courseModel.toCourseDTO()

    @Autowired
    private lateinit var courseService: CourseService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun insertTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }

    @AfterEach
    fun CleanupTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data-cleanup.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }

    @Test
    fun testKafkaProducer() {
        val message = "New book created: Test Book"
        kafkaProducer.sendMessage(Constants.COURSE_ADDED_TOPIC, message)

        val records: Iterable<ConsumerRecord<String, String>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertEquals(1, records.count(), "Expected one message")
        val record = records.first()
        assertEquals(message, record.value(), "Expected message content to match")
    }

    @Test
    fun testCourseSave() {
        assertNotNull(courseService.save(courseDTO)?.id)

        val records: Iterable<ConsumerRecord<String, String>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.asSequence().any { it.value().contains("${course.title} is created.") })
    }

    @Test
    fun testCourseUpdate() {
        val updatedTitle = "Updated test title"

        assertEquals(courseService.update(10, courseDTO.copy(title = updatedTitle)).title, updatedTitle)

        val records: Iterable<ConsumerRecord<String, String>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))

        assertTrue(records.asSequence().any { it.value().contains("Course id: 10 is updated.") })
    }


    @Test
    fun testCourseDelete() {
        assertDoesNotThrow { courseService.delete(10) }

        val records: Iterable<ConsumerRecord<String, String>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.asSequence().any { it.value().contains("Course id: 10 is delete.") })
    }

    @Test
    fun testUserSave() {
        assertNotNull(userService.save(UserDTO("newUser", "newUser@gmail.com", Role.USER, "new password"))?.id)

        val records: Iterable<ConsumerRecord<String, String>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.asSequence().any { it.value().contains("User: newUser is added.") })
    }
}
