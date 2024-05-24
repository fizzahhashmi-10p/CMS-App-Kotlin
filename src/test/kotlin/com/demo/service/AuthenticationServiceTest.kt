package com.demo.service

import com.demo.config.JwtProperties
import com.demo.dto.auth.AuthenticationRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date

internal class AuthenticationServiceTest {
    val authenticationManager: AuthenticationManager = mock(AuthenticationManager::class.java)
    val userDetailsService: UserDetailsServiceImpl = mock(UserDetailsServiceImpl::class.java)
    val tokenService: TokenService = mock(TokenService::class.java)
    val jwtProperties: JwtProperties = JwtProperties("68jxj2tzH7v82cEsH-YextKPPzN5oBfZ6_Ep8ghQR5k", 3600000)
    val authenticationService: AuthenticationService =
        AuthenticationService(
            authenticationManager,
            userDetailsService,
            tokenService,
            jwtProperties,
        )

    @Test
    fun testAuthentication() {
        val authenticationRequest = AuthenticationRequest(email = "test@example.com", password = "password")

        val userDetails: UserDetails = User.withUsername(authenticationRequest.email).password(authenticationRequest.password).build()
        val authentication: UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(userDetails, authenticationRequest.password)

        `when`(authenticationManager.authenticate(authentication)).thenReturn(authentication)
        `when`(userDetailsService.loadUserByUsername(authenticationRequest.email)).thenReturn(userDetails)

        val expirationDate = Date(System.currentTimeMillis() + jwtProperties.expiration)
        `when`(tokenService.generate(userDetails, expirationDate)).thenReturn("mockedAccessToken")

        val authenticationResponse = authenticationService.authentication(authenticationRequest)

        assertEquals("mockedAccessToken", authenticationResponse.accessToken)
    }
}
