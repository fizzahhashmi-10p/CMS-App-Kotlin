package com.demo.service

import com.demo.exception.ResourceNotFoundException
import com.demo.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

typealias ApplicationUser = com.demo.entity.User

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)
            ?.mapToUserDetails()
            ?: throw ResourceNotFoundException("User Not found!")
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User.builder()
            .username(email)
            .password(password)
            .roles(role.name)
            .build()
}
