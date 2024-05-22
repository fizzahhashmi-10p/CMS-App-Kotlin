package com.demo.repository

import com.demo.entity.User
import org.springframework.data.jpa.repository.JpaRepository

public interface UserRepository : JpaRepository<User, Long> {
    fun findAllByUsernameIn(usernames: List<String>): MutableList<User>

    fun findByEmail(email: String): User?
}
