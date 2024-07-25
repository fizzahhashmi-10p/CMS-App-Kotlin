package com.demo.controller

import com.demo.dto.auth.AuthenticationRequest
import com.google.gson.Gson
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

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
    fun testCreateUserNoAuthorization() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Gson().toJson(AuthenticationRequest("testuser@gmail.com", "password"))),
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}
