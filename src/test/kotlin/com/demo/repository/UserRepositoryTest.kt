package com.demo.repository

import com.demo.entity.User
import com.demo.util.Role
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    companion object {
        val db = PostgreSQLContainer("postgres")

        @BeforeAll
        @JvmStatic
        fun startDB() = db.start()

        @AfterAll
        @JvmStatic
        fun stopDB() = db.stop()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }

    var user = User(null, "test_user", "test_user@gmail.com", Role.USER, mutableListOf(), "password")

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun testDB() = assertTrue(db.isRunning())

    @Test
    fun testSaveUser() = assertNotNull(userRepository.save(user).id)

    @Test
    fun testFetchAllUsers() {
        userRepository.save(user)
        assertNotNull(userRepository.findAll())
    }

    @Test
    fun testFetchByEmail() {
        userRepository.save(user)
        assertEquals(userRepository.findByEmail("test_user@gmail.com")?.email, "test_user@gmail.com")
    }

    @Test
    fun testFetchByUsernameIn() {
        userRepository.save(user)
        assertTrue(userRepository.findAllByUsernameIn(listOf("test_user")).contains(user))
    }
}
