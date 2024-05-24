package com.demo.config

import com.demo.service.TokenService
import com.demo.service.UserDetailsServiceImpl
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

internal class JwtAuthenticationFilterTest {
    val tokenService: TokenService = mock(TokenService::class.java)
    val userDetailsService: UserDetailsServiceImpl = mock(UserDetailsServiceImpl::class.java)
    val jwtAuthenticationFilter: JwtAuthenticationFilter = JwtAuthenticationFilter(userDetailsService, tokenService)
    val filterChain: FilterChain = mock(FilterChain::class.java)

    @BeforeEach
    fun setUp() {
        // Clear the SecurityContextHolder before each test
        SecurityContextHolder.clearContext()
    }

    @Test
    fun testDoFilterInternal() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val token = "validToken"
        val authUser: UserDetails = User.withUsername("testuser@gmail.com").password("password").authorities(emptyList()).build()

        `when`(tokenService.extractEmail(token)).thenReturn("testuser@gmail.com")
        `when`(userDetailsService.loadUserByUsername(authUser.username)).thenReturn(authUser)
        `when`(tokenService.isValid(token, authUser)).thenReturn(true)

        request.addHeader("Authorization", "Bearer $token")
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        assertNotNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun testDoFilterInternalInvalidToken() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val invalidToken = "InvalidToken"
        val authUser: UserDetails = User.withUsername("testuser@gmail.com").password("password").authorities(emptyList()).build()

        `when`(tokenService.extractEmail(invalidToken)).thenReturn("testuser@gmail.com")
        `when`(userDetailsService.loadUserByUsername(authUser.username)).thenReturn(authUser)
        `when`(tokenService.isValid(invalidToken, authUser)).thenReturn(true)

        request.addHeader("Authorization", invalidToken)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }
}
