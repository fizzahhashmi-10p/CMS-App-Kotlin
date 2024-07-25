package com.demo.integrationTest

import com.demo.config.CourseConsumerTestConfig
import com.demo.controller.CourseController
import com.demo.dto.CourseDTO
import com.demo.dto.CourseEventDTO
import com.demo.dto.KafkaMessageDTO
import com.demo.dto.StringEventDTO
import com.demo.entity.User
import com.demo.generateTestToken
import com.demo.model.CourseModel
import com.demo.model.toCourse
import com.demo.model.toCourseDTO
import com.demo.repository.CourseRepository
import com.demo.service.CourseService
import com.demo.service.UserService
import com.demo.util.Constants.COURSE_ADDED_TOPIC
import com.demo.util.Constants.COURSE_DELETED_TOPIC
import com.demo.util.Constants.COURSE_UPDATED_TOPIC
import com.demo.util.Constants.USER_ADDED_TOPIC
import com.demo.util.Role
import com.google.gson.Gson
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EmbeddedKafka(partitions = 1, topics = [COURSE_ADDED_TOPIC, COURSE_UPDATED_TOPIC, COURSE_DELETED_TOPIC, USER_ADDED_TOPIC], brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class CourseIntegrationTest {

    companion object {
        var localStack: LocalStackContainer = LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.0")
        )

        lateinit var consumer: KafkaConsumer<String, KafkaMessageDTO>
        private lateinit var embeddedKafka: EmbeddedKafkaBroker

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
            localStack.start()

            localStack.execInContainer(
                "awslocal",
                "sqs",
                "create-queue",
                "--queue-name",
                "test-course-queue"
            )
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            embeddedKafka.destroy()
            consumer.close()
            db.stop()
            localStack.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }

    val author: User = User(10, "testuser", "testuser@gmail.com", Role.USER, mutableListOf(), "password")

    private final val courseModel =
        CourseModel(
            null,
            "Test Course",
            "Test course Description",
            true,
            mutableListOf(),
        )

    val course = courseModel.toCourse()
    val courseDTO = courseModel.toCourseDTO()

    val token = generateTestToken("testuser@gmail.com")

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var courseService: CourseService

    @Autowired
    private lateinit var courseController: CourseController

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Transactional
    @BeforeEach
    fun insertTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    @AfterEach
    fun cleanupTestData() {
        val sqlPath = Paths.get("src/test/resources/test-data-cleanup.sql")
        val sql = Files.readString(sqlPath)
        jdbcTemplate.execute(sql)
    }


    @Test
    fun testCourseCreate() {
        val request = Gson().toJson(CourseDTO(null,"New test course", "course description", mutableListOf(), false))
        mockMvc.perform(
            post("/courses")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        ).andExpect(status().isCreated)

        val records: Iterable<ConsumerRecord<String, KafkaMessageDTO>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.any {it.value() is CourseEventDTO &&
                (it.value() as CourseEventDTO).message.equals("New Topic Successfully Added") &&
                (it.value() as CourseEventDTO).course.title.equals("New test course")} )
    }


    @Test
    fun testCourseUpdate() {
        val request = Gson().toJson(
            CourseDTO(20,"Updated English Course", "Updated english course description", mutableListOf(), true))
        mockMvc.perform(
            put("/courses/20")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request),
        ).andExpect(status().isOk)

        val records: Iterable<ConsumerRecord<String, KafkaMessageDTO>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.any {it.value() is CourseEventDTO &&
                (it.value() as CourseEventDTO).message.equals("Course: 20 is updated successfully") &&
                (it.value() as CourseEventDTO).course.title.equals("Updated English Course")} )
    }


    @Test
    fun testCourseDelete() {
        mockMvc.perform(
            delete("/courses/20")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)

        val records: Iterable<ConsumerRecord<String, KafkaMessageDTO>> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertTrue(records.any {it.value() is StringEventDTO &&
                (it.value() as StringEventDTO).message.equals("Course id: 20 is deleted.")})
    }
}
