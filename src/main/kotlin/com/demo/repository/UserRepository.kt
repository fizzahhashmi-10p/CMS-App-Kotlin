package com.demo.repository

import com.demo.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

public interface UserRepository : JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM public.user WHERE username = :username", nativeQuery = true)
    fun findByUsername(
        @Param("username") username: String,
    ): User?

    fun findAllByUsernameIn(usernames: List<String>): MutableList<User>
}
