package com.demo.controller

import com.demo.dto.auth.AuthenticationRequest
import com.google.gson.Gson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup(
    Sql(scripts = ["/test-data.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS),
)
class AuthControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

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
