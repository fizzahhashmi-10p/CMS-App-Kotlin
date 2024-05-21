package com.demo.controller

import com.demo.dto.auth.AuthenticationRequest
import com.demo.dto.auth.AuthenticationResponse
import com.demo.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
) {
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest,
    ): AuthenticationResponse = authenticationService.authentication(authRequest)
}
