package com.demo.controller

import com.demo.dto.UserDTO
import com.demo.generateTestToken
import com.demo.util.Role
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
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun testGetUsersAdminAuthorization() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users")
                .header("Authorization", "Bearer " + generateTestToken("testadmin@gmail.com")),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun testGetUsersWithoutAdminAuthorization() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users")
                .header("Authorization", "Bearer " + generateTestToken("testuser@gmail.com")),
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testCreateUserNoAuthorization() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/users")
                .header("Authorization", "Bearer " + generateTestToken("testuser@gmail.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Gson().toJson(UserDTO("testuser2", "testuser2@gmail.com", Role.USER, "pass"))),
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }
}
