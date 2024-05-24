package com.demo.service

import com.demo.config.JwtProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.security.core.userdetails.User
import java.util.Date

class TokenServiceTest {
    val authUser = User.withUsername("testuser@gmail.com").password("password").roles("USER").build()
    val userDetailsService = mock(UserDetailsServiceImpl::class.java)

    val jwtProperties =
        JwtProperties("68jxj2tzH7v82cEsH-YextKPPzN5oBfZ6_Ep8ghQR5k", 3600000) // 1 hour expiration
    val tokenService = TokenService(jwtProperties)

    @Test
    fun testGenerate() =
        assertNotNull(
            tokenService.generate(
                authUser,
                Date(System.currentTimeMillis() + jwtProperties.expiration),
                emptyMap(),
            ),
        )

    @Test
    fun testIsValid() =
        assertTrue(
            tokenService.isValid(
                tokenService.generate(
                    authUser,
                    Date(System.currentTimeMillis() + jwtProperties.expiration),
                    emptyMap(),
                ),
                authUser,
            ),
        )

    @Test
    fun testIsNotValid() {
        val token =
            tokenService.generate(
                User.withUsername("invalid@gmail.com").password("password").roles("USER").build(),
                Date(System.currentTimeMillis() + jwtProperties.expiration),
                emptyMap(),
            )
        assertFalse(tokenService.isValid(token, authUser))
    }

    @Test
    fun tesEextractEmail() =
        assertEquals(
            tokenService.extractEmail(
                tokenService.generate(
                    authUser,
                    Date(System.currentTimeMillis() + jwtProperties.expiration),
                    emptyMap(),
                ),
            ),
            authUser.username,
        )

    @Test
    fun isExpired() =
        assertFalse(
            tokenService.isExpired(
                tokenService.generate(
                    authUser,
                    Date(System.currentTimeMillis() + jwtProperties.expiration),
                    emptyMap(),
                ),
            ),
        )

    @Test
    fun testGetAllClaims() =
        assertTrue(
            tokenService.getAllClaims(
                tokenService.generate(
                    authUser,
                    Date(System.currentTimeMillis() + jwtProperties.expiration),
                    mapOf("name" to "Test User Name"),
                ),
            ).containsKey("name"),
        )
}
