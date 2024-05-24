package com.demo.config

import com.demo.repository.UserRepository
import com.demo.service.UserDetailsServiceImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): DefaultSecurityFilterChain {
        @Bean
        fun userDetailsService(userRepository: UserRepository): UserDetailsService = UserDetailsServiceImpl(userRepository)

        @Bean
        fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

        @Bean
        fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
            DaoAuthenticationProvider()
                .also {
                    it.setUserDetailsService(userDetailsService(userRepository))
                    it.setPasswordEncoder(encoder())
                }

        @Bean
        fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/error") // No authorization required
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users", "/auth")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/users") // Only admin can access
                    .hasRole("ADMIN")
                    .anyRequest() // Any other request requires proper authentication
                    .fullyAuthenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
